package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

// this represents the following Java
// > assert condition ;
// > assert condition : detail ;
interface AssertExpression : Expression {
  val condition: Expression
  val detail: Expression?

  companion object {
    val FAKE_ASSERT_NAME: String = "<fake-assert-name>"
  }
}
