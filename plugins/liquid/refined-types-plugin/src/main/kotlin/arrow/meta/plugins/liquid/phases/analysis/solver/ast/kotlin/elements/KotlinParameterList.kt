package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinParameterList {
  val parameters: List<KotlinParameter>
  val ownerFunction: KotlinDeclarationWithBody?
}
