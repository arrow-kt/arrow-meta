package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface ClassOrObject : NamedDeclaration, PureClassOrObject {
  fun getAnonymousInitializers(): List<AnonymousInitializer>
  fun isTopLevel(): Boolean
  fun getPrimaryConstructorParameterList(): ParameterList? = primaryConstructor?.valueParameterList
  fun isAnnotation(): Boolean
}
