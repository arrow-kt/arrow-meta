
package arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.Type

interface Expression : Element {
  fun type(context: ResolutionContext): Type?
  fun lastBlockStatementOrThis(): Expression
}
