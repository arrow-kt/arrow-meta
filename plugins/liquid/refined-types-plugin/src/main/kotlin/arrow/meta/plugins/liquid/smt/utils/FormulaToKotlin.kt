package arrow.meta.plugins.liquid.smt.utils

import arrow.meta.plugins.liquid.smt.Solver
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.FunctionDeclarationKind
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor

interface KotlinPrinter {
  fun Formula.dumpKotlinLike(): String
  fun mirroredElement(name: String): ReferencedElement?
}

internal class DefaultKotlinPrinter(
  private val fmgr: FormulaManager,
  private val nameProvider: NameProvider
) : KotlinPrinter {

  override fun mirroredElement(name: String): ReferencedElement? =
    nameProvider.mirroredElement(name)

  override fun Formula.dumpKotlinLike(): String {
    val str = StringBuilder()
    fmgr.visit(this, KotlinPrintVisitor(fmgr, str, nameProvider))
    return str.toString()
  }

  private class KotlinPrintVisitor(
    private val fmgr: FormulaManager,
    private val out: StringBuilder,
    private val nameProvider: NameProvider
  ) : DefaultFormulaVisitor<Void?>() {

    override fun visitDefault(pF: Formula): Void? {
      nameProvider.mirroredElement(pF.toString())?.let {
        out.append(it.element.text)
      } ?: out.append(pF)
      return null
    }

    private enum class Render { Unary, Binary, Hidden, Field, Unsupported }

    private fun FunctionDeclaration<*>.toKotlin(): Pair<Render, String> =
      when (kind) {
        FunctionDeclarationKind.AND -> Render.Binary to " && "
        FunctionDeclarationKind.NOT -> Render.Unary to " ! "
        FunctionDeclarationKind.OR -> Render.Binary to " || "
        FunctionDeclarationKind.SUB -> Render.Binary to " - "
        FunctionDeclarationKind.ADD -> Render.Binary to " + "
        FunctionDeclarationKind.DIV -> Render.Binary to " / "
        FunctionDeclarationKind.MUL -> Render.Binary to " * "
        FunctionDeclarationKind.LT -> Render.Binary to " < "
        FunctionDeclarationKind.LTE -> Render.Binary to " <= "
        FunctionDeclarationKind.GT -> Render.Binary to " > "
        FunctionDeclarationKind.GTE -> Render.Binary to " >= "
        FunctionDeclarationKind.EQ -> Render.Binary to " == "
        FunctionDeclarationKind.UF ->
          when (name) {
            Solver.INT_VALUE_NAME -> Render.Hidden to ""
            Solver.BOOL_VALUE_NAME -> Render.Hidden to ""
            Solver.DECIMAL_VALUE_NAME -> Render.Hidden to ""
            Solver.FIELD_FUNCTION_NAME -> Render.Field to name
            else -> Render.Unsupported to "[unsupported UF: $name]"
          }
        else -> Render.Unsupported to "[unsupported: $this]"
      }

    override fun visitFunction(
      pF: Formula,
      pArgs: List<Formula>,
      pFunctionDeclaration: FunctionDeclaration<*>
    ): Void? {
      val (render, name) = pFunctionDeclaration.toKotlin()
      val notUF = pFunctionDeclaration.kind != FunctionDeclarationKind.UF
      if (notUF) {
        out.append("(")
      }
      when (render) {
        Render.Unary -> {
          out.append(name)
          fmgr.visit(pArgs[0], this)
        }
        Render.Binary -> {
          fmgr.visit(pArgs[0], this)
          out.append(name)
          fmgr.visit(pArgs[1], this)
        }
        Render.Unsupported -> {
          pArgs.forEach { arg ->
            fmgr.visit(arg, this)
          }
        }
        Render.Hidden -> {
          pArgs.forEach { arg ->
            fmgr.visit(arg, this)
          }
        }
        Render.Field -> {
          fmgr.visit(pArgs[1], this)
          out.append(".")
          out.append(pArgs[0].toString().substringAfterLast("."))
        }
      }
      if (notUF) {
        out.append(")")
      }
      return null
    }
  }
}
