package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface TypeReference : Annotated, AnnotationsContainer {
  val typeElement: TypeElement?
}
