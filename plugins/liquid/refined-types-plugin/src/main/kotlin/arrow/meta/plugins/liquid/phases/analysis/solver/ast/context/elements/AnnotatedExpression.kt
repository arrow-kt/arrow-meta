
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface AnnotatedExpression : Annotated, AnnotationsContainer {
  val baseExpression: Expression?
}
