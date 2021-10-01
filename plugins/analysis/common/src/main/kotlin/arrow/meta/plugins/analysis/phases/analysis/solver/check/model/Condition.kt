package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.WhenCondition

/**
 * Data type used to handle `if` and `when` without subject uniformly.
 */
sealed class Condition(val condition: Element?, val body: Expression, val whole: Element)
class SimpleCondition(val predicate: Expression?, body: Expression, whole: Element) : Condition(predicate, body, whole)
class SubjectCondition(val check: WhenCondition?, body: Expression, whole: Element) : Condition(check, body, whole)
