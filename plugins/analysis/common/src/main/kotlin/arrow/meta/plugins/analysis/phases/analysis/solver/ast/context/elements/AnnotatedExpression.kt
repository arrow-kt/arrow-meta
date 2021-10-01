
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface AnnotatedExpression : Annotated, AnnotationsContainer {
  val baseExpression: Expression?
}
