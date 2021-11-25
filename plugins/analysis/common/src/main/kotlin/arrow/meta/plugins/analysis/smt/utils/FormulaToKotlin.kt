package arrow.meta.plugins.analysis.smt.utils

import arrow.meta.plugins.analysis.smt.Solver
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.FunctionDeclarationKind
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor

interface KotlinPrinter {
  fun Formula.dumpKotlinLike(): String
  fun Formula.dumpKotlinLikeOrRemove(): String?
  fun mirroredElement(name: String): ReferencedElement?

  fun List<Formula>.dumpKotlinLike(): String =
    mapNotNull { it.dumpKotlinLikeOrRemove() }.joinToString()
}

internal class DefaultKotlinPrinter(
  private val fmgr: FormulaManager,
  private val nameProvider: NameProvider
) : KotlinPrinter {

  override fun mirroredElement(name: String): ReferencedElement? =
    nameProvider.mirroredElement(name)

  override fun Formula.dumpKotlinLike(): String {
    val str = StringBuilder()
    fmgr.visit(
      this,
      KotlinPrintVisitor(fmgr, str, nameProvider, parensContext = false, negatedContext = false)
    )
    return str.toString()
  }

  override fun Formula.dumpKotlinLikeOrRemove(): String? = dumpKotlinLike().takeIf { it != "true" }

  private data class KotlinPrintVisitor(
    private val fmgr: FormulaManager,
    private val out: StringBuilder,
    private val nameProvider: NameProvider,
    private val parensContext: Boolean,
    private val negatedContext: Boolean
  ) : DefaultFormulaVisitor<Void?>() {

    override fun visitDefault(pF: Formula): Void? {
      if (negatedContext) out.append('!')

      val text = nameProvider.mirroredElement(pF.toString())?.element?.text ?: pF.toString()
      val needsParens = (parensContext || negatedContext) && text.contains(' ')
      if (needsParens) out.append('(')
      out.append(text)
      if (needsParens) out.append(')')

      return null
    }

    private enum class Render {
      Negation,
      Postfix,
      Equality,
      Binary,
      Hidden,
      Field,
      Unsupported
    }

    private fun FunctionDeclaration<*>.toKotlin(): Triple<Render, String, String?> =
      when (kind) {
        FunctionDeclarationKind.NOT -> Triple(Render.Negation, "!", null)
        FunctionDeclarationKind.AND -> Triple(Render.Binary, "&&", null)
        FunctionDeclarationKind.OR -> Triple(Render.Binary, "||", null)
        FunctionDeclarationKind.SUB -> Triple(Render.Binary, "-", null)
        FunctionDeclarationKind.ADD -> Triple(Render.Binary, "+", null)
        FunctionDeclarationKind.DIV -> Triple(Render.Binary, "/", null)
        FunctionDeclarationKind.MUL -> Triple(Render.Binary, "*", null)
        FunctionDeclarationKind.LT -> Triple(Render.Binary, "<", ">=")
        FunctionDeclarationKind.LTE -> Triple(Render.Equality, "<=", ">")
        FunctionDeclarationKind.GT -> Triple(Render.Binary, ">", "<=")
        FunctionDeclarationKind.GTE -> Triple(Render.Equality, ">=", "<")
        FunctionDeclarationKind.EQ -> Triple(Render.Equality, "==", "!=")
        FunctionDeclarationKind.UF ->
          when (name) {
            Solver.INT_VALUE_NAME -> Triple(Render.Hidden, "", null)
            Solver.BOOL_VALUE_NAME -> Triple(Render.Hidden, "", null)
            Solver.DECIMAL_VALUE_NAME -> Triple(Render.Hidden, "", null)
            Solver.FIELD_FUNCTION_NAME -> Triple(Render.Field, name, null)
            Solver.IS_NULL_FUNCTION_NAME -> Triple(Render.Postfix, " == null", " != null")
            else -> Triple(Render.Unsupported, "[unsupported UF: $name]", null)
          }
        else -> Triple(Render.Unsupported, "[unsupported: $this]", null)
      }

    override fun visitFunction(
      pF: Formula,
      pArgs: List<Formula>,
      pFunctionDeclaration: FunctionDeclaration<*>
    ): Void? {
      val (render, name, negatedName) = pFunctionDeclaration.toKotlin()
      when (render) {
        Render.Hidden, Render.Unsupported -> {
          pArgs.forEach { arg -> fmgr.visit(arg, this) }
        }
        Render.Negation -> {
          fmgr.visit(pArgs[0], this.copy(negatedContext = !negatedContext))
        }
        Render.Equality -> {
          val leftStringBuilder = StringBuilder()
          fmgr.visit(
            pArgs[0],
            this.copy(out = leftStringBuilder, parensContext = true, negatedContext = false)
          )
          val rightStringBuilder = StringBuilder()
          fmgr.visit(
            pArgs[1],
            this.copy(out = rightStringBuilder, parensContext = true, negatedContext = false)
          )

          if (leftStringBuilder.toString() == rightStringBuilder.toString()) {
            out.append(if (negatedContext) "false" else "true")
          } else {
            if (parensContext) out.append('(')
            out.append(leftStringBuilder)
            out.append(' ')
            out.append(if (negatedContext) negatedName else name)
            out.append(' ')
            out.append(rightStringBuilder)
            if (parensContext) out.append(')')
          }
        }
        else -> {
          val mustBeNegated = negatedContext && negatedName == null
          val needsParens = parensContext || mustBeNegated
          val nameToShow = if (negatedContext && negatedName != null) negatedName else name

          if (mustBeNegated) out.append('!')
          if (needsParens) out.append('(')
          when (render) {
            Render.Postfix -> {
              fmgr.visit(pArgs[0], this.copy(parensContext = false, negatedContext = false))
              out.append(nameToShow)
            }
            Render.Binary -> {
              fmgr.visit(pArgs[0], this.copy(parensContext = true, negatedContext = false))
              out.append(' ')
              out.append(nameToShow)
              out.append(' ')
              fmgr.visit(pArgs[1], this.copy(parensContext = true, negatedContext = false))
            }
            Render.Field -> {
              fmgr.visit(pArgs[1], this.copy(parensContext = false, negatedContext = false))
              out.append(".")
              out.append(pArgs[0].toString().substringAfterLast("."))
            }
            else -> {} // taken care of above
          }
          if (needsParens) out.append(')')
        }
      }
      return null
    }
  }
}
