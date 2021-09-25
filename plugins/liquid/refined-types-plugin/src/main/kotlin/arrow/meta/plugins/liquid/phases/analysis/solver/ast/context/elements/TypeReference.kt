package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface TypeReference : Annotated, AnnotationsContainer {
  val typeElement: TypeElement?
}
