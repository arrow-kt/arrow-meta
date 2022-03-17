@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.AssignmentExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.CompoundAssignmentTree
import com.sun.source.tree.Tree
import com.sun.tools.javac.tree.JCTree

public class JavaAssignment(private val ctx: AnalysisContext, private val impl: AssignmentTree) :
  AssignmentExpression, JavaElement(ctx, impl) {
  override val left: Expression
    get() = impl.variable.model(ctx)
  override val right: Expression
    get() = impl.expression.model(ctx)

  // these three come from the Kotlin representation
  override val operationToken: String = "="
  override val operationTokenRpr: String = "EQ"
  override val operationReference: SimpleNameExpression
    get() = JavaFakeReference("=", this)
}

// the compound assignments "var op= exp"
// are translated as "var = var op exp"
public class JavaCompoundAssignment(
  private val ctx: AnalysisContext,
  private val impl: CompoundAssignmentTree
) : AssignmentExpression, JavaElement(ctx, impl) {
  override val left: Expression
    get() = impl.variable.model(ctx)
  override val right: Expression
    get() = JavaCompoundAssignmentRHS(ctx, impl)

  // these three come from the Kotlin representation
  override val operationToken: String = "="
  override val operationTokenRpr: String = "EQ"
  override val operationReference: SimpleNameExpression
    get() = JavaFakeReference("=", this)
}

public class JavaCompoundAssignmentRHS(
  private val ctx: AnalysisContext,
  private val impl: CompoundAssignmentTree
) : BinaryExpression, JavaElement(ctx, impl) {

  private val operatorName: String = compoundkindNames[impl.kind] ?: "UNKNOWN"
  override val operationReference: SimpleNameExpression
    get() = JavaFakeReference(ctx.elements.getName(operatorName).toString(), this)

  override val operationToken: String
    get() =
      when (impl) {
        is JCTree.JCAssignOp -> impl.operator.name.toString()
        else -> operatorName
      }
  override val operationTokenRpr: String
    get() = operatorName
  override val left: Expression
    get() = impl.variable.model(ctx)
  override val right: Expression
    get() = impl.expression.model(ctx)
}

internal val compoundkindNames: Map<Tree.Kind, String> =
  mapOf(
    Tree.Kind.MULTIPLY_ASSIGNMENT to "MUL",
    Tree.Kind.DIVIDE_ASSIGNMENT to "DIV",
    Tree.Kind.REMAINDER_ASSIGNMENT to "REM",
    Tree.Kind.PLUS_ASSIGNMENT to "PLUS",
    Tree.Kind.MINUS_ASSIGNMENT to "MINUS",
    Tree.Kind.LEFT_SHIFT_ASSIGNMENT to "SHIFTL", // invented
    Tree.Kind.RIGHT_SHIFT_ASSIGNMENT to "SHIFTR", // invented
    Tree.Kind.UNSIGNED_RIGHT_SHIFT_ASSIGNMENT to "USHIFTR", // invented
    Tree.Kind.AND_ASSIGNMENT to "BITAND",
    Tree.Kind.OR_ASSIGNMENT to "BITOR",
    Tree.Kind.XOR_ASSIGNMENT to "BITXOR",
  )
