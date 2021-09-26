
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Annotated : Element {
  fun getAnnotations(): List<Annotation>
  val annotationEntries: List<AnnotationEntry>
}
