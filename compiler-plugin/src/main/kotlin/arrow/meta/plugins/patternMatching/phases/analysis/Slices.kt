package arrow.meta.plugins.patternMatching.phases.analysis

import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.util.slicedMap.Slices


val PATTERN_EXPRESSION_CAPTURED_PARAMS = Slices.createCollectiveSetSlice<KtSimpleNameExpression>()
val PATTERN_EXPRESSION_BODY_PARAMS = Slices.createCollectiveSetSlice<KtSimpleNameExpression>()
