
package arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.Type

interface Expression : Element {
  fun type(context: ResolutionContext): Type?
  fun lastBlockStatementOrThis(): Expression
}
