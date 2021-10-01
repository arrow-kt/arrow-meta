package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

enum class AnnotationUseSiteTarget {
  FIELD,
  FILE,
  PROPERTY,
  PROPERTY_GETTER,
  PROPERTY_SETTER,
  RECEIVER,
  CONSTRUCTOR_PARAMETER,
  SETTER_PARAMETER,
  PROPERTY_DELEGATE_FIELD
}
