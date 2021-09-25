package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinModifierList : KotlinAnnotationsContainer {
  val annotations: List<KotlinAnnotation>
  val annotationEntries: List<KotlinAnnotationEntry>
}
