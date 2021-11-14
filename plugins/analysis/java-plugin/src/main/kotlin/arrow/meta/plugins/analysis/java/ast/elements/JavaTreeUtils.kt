@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import com.sun.source.tree.AnnotatedTypeTree
import com.sun.source.tree.AnnotationTree
import com.sun.source.tree.ArrayAccessTree
import com.sun.source.tree.ArrayTypeTree
import com.sun.source.tree.AssertTree
import com.sun.source.tree.AssignmentTree
import com.sun.source.tree.BinaryTree
import com.sun.source.tree.BlockTree
import com.sun.source.tree.BreakTree
import com.sun.source.tree.CaseTree
import com.sun.source.tree.CatchTree
import com.sun.source.tree.ClassTree
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.CompoundAssignmentTree
import com.sun.source.tree.ConditionalExpressionTree
import com.sun.source.tree.ContinueTree
import com.sun.source.tree.DoWhileLoopTree
import com.sun.source.tree.EmptyStatementTree
import com.sun.source.tree.EnhancedForLoopTree
import com.sun.source.tree.ErroneousTree
import com.sun.source.tree.ExportsTree
import com.sun.source.tree.ExpressionStatementTree
import com.sun.source.tree.ForLoopTree
import com.sun.source.tree.IdentifierTree
import com.sun.source.tree.IfTree
import com.sun.source.tree.ImportTree
import com.sun.source.tree.InstanceOfTree
import com.sun.source.tree.IntersectionTypeTree
import com.sun.source.tree.LabeledStatementTree
import com.sun.source.tree.LambdaExpressionTree
import com.sun.source.tree.LiteralTree
import com.sun.source.tree.MemberReferenceTree
import com.sun.source.tree.MemberSelectTree
import com.sun.source.tree.MethodInvocationTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.ModifiersTree
import com.sun.source.tree.ModuleTree
import com.sun.source.tree.NewArrayTree
import com.sun.source.tree.NewClassTree
import com.sun.source.tree.OpensTree
import com.sun.source.tree.PackageTree
import com.sun.source.tree.ParameterizedTypeTree
import com.sun.source.tree.ParenthesizedTree
import com.sun.source.tree.PrimitiveTypeTree
import com.sun.source.tree.ProvidesTree
import com.sun.source.tree.RequiresTree
import com.sun.source.tree.ReturnTree
import com.sun.source.tree.SwitchTree
import com.sun.source.tree.SynchronizedTree
import com.sun.source.tree.ThrowTree
import com.sun.source.tree.Tree
import com.sun.source.tree.TreeVisitor
import com.sun.source.tree.TryTree
import com.sun.source.tree.TypeCastTree
import com.sun.source.tree.TypeParameterTree
import com.sun.source.tree.UnaryTree
import com.sun.source.tree.UnionTypeTree
import com.sun.source.tree.UsesTree
import com.sun.source.tree.VariableTree
import com.sun.source.tree.WhileLoopTree
import com.sun.source.tree.WildcardTree
import com.sun.source.util.SimpleTreeVisitor

public typealias OurTreeVisitor<R> = SimpleTreeVisitor<R, Unit>

public fun <R> Tree.visit(visitor: OurTreeVisitor<R>): R = this.accept(visitor, Unit)

public fun <P> Tree.visitRecursively(visitor: TreeVisitor<Unit, P>, value: P): Unit =
  this.accept(RecursiveTreeVisitor(visitor), value)

public fun Tree.visitRecursively(visitor: TreeVisitor<Unit, Unit>): Unit =
  this.accept(RecursiveTreeVisitor(visitor), Unit)

public fun <R, P> Tree?.accept(visitor: TreeVisitor<R, P>, value: P): R? =
  this?.accept(visitor, value)

public fun <R, P> Iterable<Tree>.accept(visitor: TreeVisitor<R, P>, value: P): List<R> =
  this.map { it.accept(visitor, value) }

public class RecursiveTreeVisitor<P>(private val underlying: TreeVisitor<Unit, P>) :
  TreeVisitor<Unit, P> {
  override fun visitAnnotatedType(node: AnnotatedTypeTree?, p: P) {
    node?.annotations?.accept(this, p)
    underlying.visitAnnotatedType(node, p)
  }

  override fun visitAnnotation(node: AnnotationTree?, p: P) {
    node?.annotationType?.accept(this, p)
    node?.arguments?.accept(this, p)
    underlying.visitAnnotation(node, p)
  }

  override fun visitMethodInvocation(node: MethodInvocationTree?, p: P) {
    node?.typeArguments?.accept(this, p)
    node?.methodSelect?.accept(this, p)
    node?.arguments?.accept(this, p)
    underlying.visitMethodInvocation(node, p)
  }

  override fun visitAssert(node: AssertTree?, p: P) {
    node?.condition?.accept(this, p)
    node?.detail?.accept(this, p)
    underlying.visitAssert(node, p)
  }

  override fun visitAssignment(node: AssignmentTree?, p: P) {
    node?.variable?.accept(this, p)
    node?.expression?.accept(this, p)
    underlying.visitAssignment(node, p)
  }

  override fun visitCompoundAssignment(node: CompoundAssignmentTree?, p: P) {
    node?.variable?.accept(this, p)
    node?.expression?.accept(this, p)
    underlying.visitCompoundAssignment(node, p)
  }

  override fun visitBinary(node: BinaryTree?, p: P) {
    node?.leftOperand?.accept(this, p)
    node?.rightOperand?.accept(this, p)
    underlying.visitBinary(node, p)
  }

  override fun visitBlock(node: BlockTree?, p: P) {
    node?.statements?.accept(this, p)
    underlying.visitBlock(node, p)
  }

  override fun visitBreak(node: BreakTree?, p: P) {
    underlying.visitBreak(node, p)
  }

  override fun visitCase(node: CaseTree?, p: P) {
    node?.expression?.accept(this, p)
    node?.statements?.accept(this, p)
    underlying.visitCase(node, p)
  }

  override fun visitCatch(node: CatchTree?, p: P) {
    node?.parameter?.accept(this, p)
    node?.block?.accept(this, p)
    underlying.visitCatch(node, p)
  }

  override fun visitClass(node: ClassTree?, p: P) {
    node?.modifiers?.accept(this, p)
    node?.typeParameters?.accept(this, p)
    node?.extendsClause?.accept(this, p)
    node?.implementsClause?.accept(this, p)
    node?.members?.accept(this, p)
    underlying.visitClass(node, p)
  }

  override fun visitConditionalExpression(node: ConditionalExpressionTree?, p: P) {
    node?.condition?.accept(this, p)
    node?.trueExpression?.accept(this, p)
    node?.falseExpression?.accept(this, p)
    underlying.visitConditionalExpression(node, p)
  }

  override fun visitContinue(node: ContinueTree?, p: P) {
    underlying.visitContinue(node, p)
  }

  override fun visitDoWhileLoop(node: DoWhileLoopTree?, p: P) {
    node?.condition?.accept(this, p)
    node?.statement?.accept(this, p)
    underlying.visitDoWhileLoop(node, p)
  }

  override fun visitErroneous(node: ErroneousTree?, p: P) {
    node?.errorTrees?.accept(this, p)
    underlying.visitErroneous(node, p)
  }

  override fun visitExpressionStatement(node: ExpressionStatementTree?, p: P) {
    node?.expression?.accept(this, p)
    underlying.visitExpressionStatement(node, p)
  }

  override fun visitEnhancedForLoop(node: EnhancedForLoopTree?, p: P) {
    node?.variable?.accept(this, p)
    node?.expression?.accept(this, p)
    node?.statement?.accept(this, p)
    underlying.visitEnhancedForLoop(node, p)
  }

  override fun visitForLoop(node: ForLoopTree?, p: P) {
    node?.initializer?.accept(this, p)
    node?.condition?.accept(this, p)
    node?.update?.accept(this, p)
    node?.statement?.accept(this, p)
    underlying.visitForLoop(node, p)
  }

  override fun visitIdentifier(node: IdentifierTree?, p: P) {
    underlying.visitIdentifier(node, p)
  }

  override fun visitIf(node: IfTree?, p: P) {
    node?.condition?.accept(this, p)
    node?.thenStatement?.accept(this, p)
    node?.elseStatement?.accept(this, p)
    underlying.visitIf(node, p)
  }

  override fun visitImport(node: ImportTree?, p: P) {
    node?.qualifiedIdentifier?.accept(this, p)
    underlying.visitImport(node, p)
  }

  override fun visitArrayAccess(node: ArrayAccessTree?, p: P) {
    node?.expression?.accept(this, p)
    node?.index?.accept(this, p)
    underlying.visitArrayAccess(node, p)
  }

  override fun visitLabeledStatement(node: LabeledStatementTree?, p: P) {
    node?.statement?.accept(this, p)
    underlying.visitLabeledStatement(node, p)
  }

  override fun visitLiteral(node: LiteralTree?, p: P) {
    underlying.visitLiteral(node, p)
  }

  override fun visitMethod(node: MethodTree?, p: P) {
    node?.modifiers?.accept(this, p)
    node?.returnType?.accept(this, p)
    node?.typeParameters?.accept(this, p)
    node?.parameters?.accept(this, p)
    node?.throws?.accept(this, p)
    node?.body?.accept(this, p)
    underlying.visitMethod(node, p)
  }

  override fun visitModifiers(node: ModifiersTree?, p: P) {
    node?.annotations?.accept(this, p)
    underlying.visitModifiers(node, p)
  }

  override fun visitNewArray(node: NewArrayTree?, p: P) {
    node?.dimensions?.accept(this, p)
    node?.initializers?.accept(this, p)
    node?.annotations?.accept(this, p)
    node?.dimAnnotations?.forEach { it.accept(this, p) }
    underlying.visitNewArray(node, p)
  }

  override fun visitNewClass(node: NewClassTree?, p: P) {
    node?.typeArguments?.accept(this, p)
    node?.identifier?.accept(this, p)
    node?.arguments?.accept(this, p)
    node?.classBody?.accept(this, p)
    underlying.visitNewClass(node, p)
  }

  override fun visitLambdaExpression(node: LambdaExpressionTree?, p: P) {
    node?.parameters?.accept(this, p)
    node?.body?.accept(this, p)
    underlying.visitLambdaExpression(node, p)
  }

  override fun visitPackage(node: PackageTree?, p: P) {
    node?.annotations?.accept(this, p)
    node?.packageName?.accept(this, p)
    underlying.visitPackage(node, p)
  }

  override fun visitParenthesized(node: ParenthesizedTree?, p: P) {
    node?.expression?.accept(this, p)
    underlying.visitParenthesized(node, p)
  }

  override fun visitReturn(node: ReturnTree?, p: P) {
    node?.expression?.accept(this, p)
    underlying.visitReturn(node, p)
  }

  override fun visitMemberSelect(node: MemberSelectTree?, p: P) {
    node?.expression?.accept(this, p)
    underlying.visitMemberSelect(node, p)
  }

  override fun visitMemberReference(node: MemberReferenceTree?, p: P) {
    node?.qualifierExpression?.accept(this, p)
    node?.typeArguments?.accept(this, p)
    underlying.visitMemberReference(node, p)
  }

  override fun visitEmptyStatement(node: EmptyStatementTree?, p: P) {
    underlying.visitEmptyStatement(node, p)
  }

  override fun visitSwitch(node: SwitchTree?, p: P) {
    node?.expression?.accept(this, p)
    node?.cases?.accept(this, p)
    underlying.visitSwitch(node, p)
  }

  override fun visitSynchronized(node: SynchronizedTree?, p: P) {
    node?.expression?.accept(this, p)
    node?.block?.accept(this, p)
    underlying.visitSynchronized(node, p)
  }

  override fun visitThrow(node: ThrowTree?, p: P) {
    node?.expression?.accept(this, p)
    underlying.visitThrow(node, p)
  }

  override fun visitCompilationUnit(node: CompilationUnitTree?, p: P) {
    node?.packageAnnotations?.accept(this, p)
    node?.`package`?.accept(this, p)
    node?.imports?.accept(this, p)
    node?.typeDecls?.accept(this, p)
    underlying.visitCompilationUnit(node, p)
  }

  override fun visitTry(node: TryTree?, p: P) {
    node?.block?.accept(this, p)
    node?.catches?.accept(this, p)
    node?.finallyBlock?.accept(this, p)
    node?.resources?.accept(this, p)
    underlying.visitTry(node, p)
  }

  override fun visitParameterizedType(node: ParameterizedTypeTree?, p: P) {
    node?.type?.accept(this, p)
    node?.typeArguments?.accept(this, p)
    underlying.visitParameterizedType(node, p)
  }

  override fun visitUnionType(node: UnionTypeTree?, p: P) {
    node?.typeAlternatives?.accept(this, p)
    underlying.visitUnionType(node, p)
  }

  override fun visitIntersectionType(node: IntersectionTypeTree?, p: P) {
    node?.bounds?.accept(this, p)
    underlying.visitIntersectionType(node, p)
  }

  override fun visitArrayType(node: ArrayTypeTree?, p: P) {
    node?.type?.accept(this, p)
    underlying.visitArrayType(node, p)
  }

  override fun visitTypeCast(node: TypeCastTree?, p: P) {
    node?.expression?.accept(this, p)
    node?.type?.accept(this, p)
    underlying.visitTypeCast(node, p)
  }

  override fun visitPrimitiveType(node: PrimitiveTypeTree?, p: P) {
    underlying.visitPrimitiveType(node, p)
  }

  override fun visitTypeParameter(node: TypeParameterTree?, p: P) {
    node?.bounds?.accept(this, p)
    underlying.visitTypeParameter(node, p)
  }

  override fun visitInstanceOf(node: InstanceOfTree?, p: P) {
    node?.expression?.accept(this, p)
    node?.type?.accept(this, p)
    underlying.visitInstanceOf(node, p)
  }

  override fun visitUnary(node: UnaryTree?, p: P) {
    node?.expression?.accept(this, p)
    underlying.visitUnary(node, p)
  }

  override fun visitVariable(node: VariableTree?, p: P) {
    node?.modifiers?.accept(this, p)
    node?.nameExpression?.accept(this, p)
    node?.type?.accept(this, p)
    node?.initializer?.accept(this, p)
    underlying.visitVariable(node, p)
  }

  override fun visitWhileLoop(node: WhileLoopTree?, p: P) {
    node?.condition?.accept(this, p)
    node?.statement?.accept(this, p)
    underlying.visitWhileLoop(node, p)
  }

  override fun visitWildcard(node: WildcardTree?, p: P) {
    node?.bound?.accept(this, p)
    underlying.visitWildcard(node, p)
  }

  override fun visitModule(node: ModuleTree?, p: P) {
    node?.annotations?.accept(this, p)
    node?.name?.accept(this, p)
    node?.directives?.accept(this, p)
    underlying.visitModule(node, p)
  }

  override fun visitExports(node: ExportsTree?, p: P) {
    node?.packageName?.accept(this, p)
    node?.moduleNames?.accept(this, p)
    underlying.visitExports(node, p)
  }

  override fun visitOpens(node: OpensTree?, p: P) {
    node?.packageName?.accept(this, p)
    node?.moduleNames?.accept(this, p)
    underlying.visitOpens(node, p)
  }

  override fun visitProvides(node: ProvidesTree?, p: P) {
    node?.serviceName?.accept(this, p)
    node?.implementationNames?.accept(this, p)
    underlying.visitProvides(node, p)
  }

  override fun visitRequires(node: RequiresTree?, p: P) {
    node?.moduleName?.accept(this, p)
    underlying.visitRequires(node, p)
  }

  override fun visitUses(node: UsesTree?, p: P) {
    node?.serviceName?.accept(this, p)
    underlying.visitUses(node, p)
  }

  override fun visitOther(node: Tree?, p: P) {
    underlying.visitOther(node, p)
  }
}
