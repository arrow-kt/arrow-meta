package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface Class : ClassOrObject {
  fun getProperties(): List<Property>
  fun isInterface(): Boolean
  fun isEnum(): Boolean
  fun isData(): Boolean
  fun isSealed(): Boolean
  fun isInner(): Boolean
  fun isInline(): Boolean
  fun isValue(): Boolean
}
