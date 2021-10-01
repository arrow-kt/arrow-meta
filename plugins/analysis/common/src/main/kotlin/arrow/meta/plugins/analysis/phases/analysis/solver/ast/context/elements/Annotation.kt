package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface Annotation {
  val entries: List<AnnotationEntry>
  val useSiteTarget: AnnotationUseSiteTarget?
}
