package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ModifierList : AnnotationsContainer {
  val annotations: List<Annotation>
  val annotationEntries: List<AnnotationEntry>
}
