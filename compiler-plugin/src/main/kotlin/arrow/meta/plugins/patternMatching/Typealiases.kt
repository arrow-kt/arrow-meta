package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo

typealias ExpressionPattern = List<(KtExpression) -> Boolean>
typealias BindingTraceEntry = MutableMap.MutableEntry<KtExpression, KotlinTypeInfo>
typealias BindingTracePattern = List<(BindingTraceEntry) -> Boolean>
