@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.CallExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ExpressionLambdaArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.OperationExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.SimpleNameExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeProjection
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.UnaryExpression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.ValueArgument
import com.sun.source.tree.BinaryTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.source.tree.NewClassTree
import com.sun.source.tree.Tree
import com.sun.source.tree.UnaryTree
import com.sun.tools.javac.tree.JCTree

public class JavaCall(private val ctx: AnalysisContext, private val impl: MethodInvocationTree) :
  CallExpression, JavaElement(ctx, impl) {
  override val calleeExpression: Expression?
    get() = this.getResolvedCall()?.getReceiverExpression()
  override val typeArguments: List<TypeProjection>
    get() = impl.typeArguments.mapNotNull { JavaTypeProjection(ctx, it) }
  override val valueArguments: List<ValueArgument>
    get() = this.getResolvedCall()?.valueArguments?.flatMap { it.value.arguments }.orEmpty()
  // Java does not have final block arguments
  override val lambdaArguments: List<ExpressionLambdaArgument>
    get() = emptyList()
}

public class JavaConstructorCall(private val ctx: AnalysisContext, private val impl: NewClassTree) :
  CallExpression, JavaElement(ctx, impl) {
  override val calleeExpression: Expression? = null
  override val typeArguments: List<TypeProjection>
    get() = impl.typeArguments.mapNotNull { JavaTypeProjection(ctx, it) }
  override val valueArguments: List<ValueArgument>
    get() = this.getResolvedCall()?.valueArguments?.flatMap { it.value.arguments }.orEmpty()
  // Java does not have final block arguments
  override val lambdaArguments: List<ExpressionLambdaArgument>
    get() = emptyList()
}

public open class JavaOperation(private val ctx: AnalysisContext, impl: Tree) :
  OperationExpression, JavaElement(ctx, impl) {
  protected val operatorName: String = kindNames[impl.kind] ?: "UNKNOWN"
  override val operationReference: SimpleNameExpression
    get() = JavaFakeReference(ctx.elements.getName(operatorName).toString(), this)
}

public class JavaUnary(private val ctx: AnalysisContext, private val impl: UnaryTree) :
  UnaryExpression, JavaOperation(ctx, impl) {
  override val baseExpression: Expression
    get() = impl.expression.model(ctx)
}

public class JavaBinary(private val ctx: AnalysisContext, private val impl: BinaryTree) :
  BinaryExpression, JavaOperation(ctx, impl) {
  override val operationToken: String
    get() =
      when (impl) {
        is JCTree.JCOperatorExpression -> impl.operator.name.toString()
        else -> operatorName
      }
  override val operationTokenRpr: String
    get() = operatorName
  override val left: Expression
    get() = impl.leftOperand.model(ctx)
  override val right: Expression
    get() = impl.rightOperand.model(ctx)
}

// for the names look at [org.jetbrains.kotlin.lexer.KtTokens]
internal val kindNames: Map<Tree.Kind, String> =
  mapOf(
    Tree.Kind.POSTFIX_INCREMENT to "PLUSPLUS",
    Tree.Kind.POSTFIX_DECREMENT to "MINUSMINUS",
    Tree.Kind.PREFIX_INCREMENT to "PLUSPLUS",
    Tree.Kind.PREFIX_DECREMENT to "MINUSMINUS",
    Tree.Kind.UNARY_PLUS to "PLUS",
    Tree.Kind.UNARY_MINUS to "MINUS",
    Tree.Kind.BITWISE_COMPLEMENT to "BITEXCL", // invented
    Tree.Kind.LOGICAL_COMPLEMENT to "EXCL",
    Tree.Kind.MULTIPLY to "MUL",
    Tree.Kind.DIVIDE to "DIV",
    Tree.Kind.REMAINDER to "REM",
    Tree.Kind.PLUS to "PLUS",
    Tree.Kind.MINUS to "MINUS",
    Tree.Kind.LEFT_SHIFT to "SHIFTL", // invented
    Tree.Kind.RIGHT_SHIFT to "SHIFTR", // invented
    Tree.Kind.UNSIGNED_RIGHT_SHIFT to "USHIFTR", // invented
    Tree.Kind.LESS_THAN to "LT",
    Tree.Kind.LESS_THAN_EQUAL to "LTEQ",
    Tree.Kind.GREATER_THAN to "GT",
    Tree.Kind.GREATER_THAN_EQUAL to "GTEQ",
    Tree.Kind.EQUAL_TO to "EQEQ",
    Tree.Kind.NOT_EQUAL_TO to "EXCLEQ",
    Tree.Kind.AND to "BITAND",
    Tree.Kind.OR to "BITOR",
    Tree.Kind.XOR to "BITXOR",
    Tree.Kind.CONDITIONAL_AND to "ANDAND",
    Tree.Kind.CONDITIONAL_OR to "OROR"
  )
