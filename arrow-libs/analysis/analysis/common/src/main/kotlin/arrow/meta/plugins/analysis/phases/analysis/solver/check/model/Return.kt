package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

/** Ways to return from a block */
sealed interface Return

/** This encompasses all possible was to exit a block: 'return', 'break', 'continue' */
sealed interface ExplicitReturn : Return

/** The block exited via its last statement, or has not exited yet */
object NoReturn : Return

// 2.0: data for the checks
// ------------------------

/** Explicit 'return', maybe with a name */
data class ExplicitBlockReturn(val returnPoint: String?) : ExplicitReturn

/** 'break' or 'continue' inside a loop */
data class ExplicitLoopReturn(val returnPoint: String?) : ExplicitReturn

/** 'throw' */
data class ExplicitThrowReturn(val exceptionType: Type?) : ExplicitReturn
