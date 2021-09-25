package arrow.meta.plugins.liquid.phases.analysis.solver.ast.kotlin.elements

interface KotlinFunctionLiteral : KotlinFunction {
  fun hasParameterSpecification(): Boolean
  override val bodyExpression: KotlinExpression?
}
