package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenCondition

/**
 * Data type used to handle `if` and `when` without subject uniformly.
 */
sealed class Condition(val condition: Element?, val isElse: Boolean, val body: Expression, val whole: Element)
class SimpleCondition(val predicate: Expression?, isElse: Boolean, body: Expression, whole: Element) : Condition(predicate, isElse, body, whole)
class SubjectCondition(val check: WhenCondition?, isElse: Boolean, body: Expression, whole: Element) : Condition(check, isElse, body, whole)
