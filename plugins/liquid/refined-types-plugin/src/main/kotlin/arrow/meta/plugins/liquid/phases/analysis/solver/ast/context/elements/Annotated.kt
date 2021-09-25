
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Annotated : Element {
  val annotations: List<Annotation>
  val annotationEntries: List<AnnotationEntry>
}
