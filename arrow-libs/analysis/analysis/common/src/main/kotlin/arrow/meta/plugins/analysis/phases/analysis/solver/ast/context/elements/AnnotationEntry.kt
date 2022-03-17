package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface AnnotationEntry : CallElement {
  val typeReference: TypeReference?
  val useSiteTarget: AnnotationUseSiteTarget?
  val shortName: Name?
}
