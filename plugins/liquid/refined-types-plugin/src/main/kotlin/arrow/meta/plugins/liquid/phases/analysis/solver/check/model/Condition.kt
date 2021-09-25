package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.Expression

/**
 * Data type used to handle `if` and `when` without subject uniformly.
 */
data class Condition(val condition: Expression?, val body: Expression, val whole: Element)
