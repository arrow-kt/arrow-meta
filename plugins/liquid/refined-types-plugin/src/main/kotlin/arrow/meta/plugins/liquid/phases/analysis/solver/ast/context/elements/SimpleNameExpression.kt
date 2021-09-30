package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

interface SimpleNameExpression : ReferenceExpression {
    fun getReferencedName(): String
    fun getReferencedNameAsName(): Name
}
