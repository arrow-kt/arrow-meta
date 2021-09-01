package arrow.meta.plugins.liquid.smt.utils

import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaManager
import org.sosy_lab.java_smt.api.FunctionDeclaration
import org.sosy_lab.java_smt.api.FunctionDeclarationKind
import org.sosy_lab.java_smt.api.visitors.DefaultFormulaVisitor

interface KotlinPrinter {
  fun Formula.dumpKotlinLike(): String
}

internal class DefaultKotlinPrinter(private val fmgr: FormulaManager) : KotlinPrinter {

  override fun Formula.dumpKotlinLike(): String {
    val str = StringBuilder()
    fmgr.visit(this, KotlinPrintVisitor(fmgr, str))
    return str.toString()
  }

  private class KotlinPrintVisitor(
    private val fmgr: FormulaManager,
    private val out: StringBuilder
  ) : DefaultFormulaVisitor<Void?>() {

    override fun visitDefault(pF: Formula): Void? {
      out.append(pF)
      return null
    }

    private enum class Render { Unary, Binary, Unsupported }

    private fun FunctionDeclarationKind.toKotlin(): Pair<Render, String> =
      when (this) {
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
        else -> Render.Unsupported to "[unsupported: $this]"
      }

    override fun visitFunction(
      pF: Formula,
      pArgs: List<Formula>,
      pFunctionDeclaration: FunctionDeclaration<*>
    ): Void? {
      val (render, name) = pFunctionDeclaration.kind.toKotlin()
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
      }
      if (notUF) {
        out.append(")")
      }
      return null
    }
  }
}
