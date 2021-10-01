package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

interface SimpleNameExpression : ReferenceExpression {
    fun getReferencedName(): String
    fun getReferencedNameAsName(): Name
}
