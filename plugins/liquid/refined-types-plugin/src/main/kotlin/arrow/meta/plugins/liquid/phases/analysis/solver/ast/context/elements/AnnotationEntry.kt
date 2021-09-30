package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface AnnotationEntry : CallElement {
  val typeReference: TypeReference?
  val useSiteTarget: AnnotationUseSiteTarget?
  val shortName: Name?
}
