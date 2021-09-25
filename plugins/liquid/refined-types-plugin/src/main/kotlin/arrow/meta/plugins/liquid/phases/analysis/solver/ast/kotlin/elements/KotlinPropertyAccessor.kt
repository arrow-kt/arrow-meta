package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinPropertyAccessor : KotlinDeclarationWithBody, KotlinModifierListOwner,
  KotlinDeclarationWithInitializer {
  val isSetter: Boolean
  val isGetter: Boolean
  val parameterList: KotlinParameterList?
  val parameter: KotlinParameter?
  val returnTypeReference: KotlinTypeReference?
  val property: KotlinProperty
}
