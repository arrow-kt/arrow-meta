package arrow.meta.plugins.analysis.phases.analysis.solver.check.model

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.smt.ObjectFormula
import arrow.meta.plugins.analysis.smt.Solver
import org.sosy_lab.java_smt.api.BooleanFormula

data class CheckData(
  val context: ResolutionContext,
  val returnPoints: ReturnPoints,
  val varInfo: CurrentVarInfo,
  val branch: CurrentBranch
) {
  fun addReturnPoint(scope: String, variableName: ObjectFormula) =
    this.copy(returnPoints = returnPoints.addAndReplaceTopMost(scope, variableName))

  fun replaceTopMostReturnPoint(scope: String?, variableName: ObjectFormula) =
    this.copy(returnPoints = returnPoints.replaceTopMost(scope, variableName))

  fun addVarInfo(
    solver: Solver,
    name: String,
    smtName: String,
    origin: Element,
    invariant: BooleanFormula? = null
  ): CheckData = this.copy(varInfo = varInfo.add(solver, name, smtName, origin, invariant))

  fun addVarInfos(vars: List<VarInfo>): CheckData = this.copy(varInfo = varInfo.add(vars))

  fun addBranch(constraint: BooleanFormula): CheckData = this.copy(branch = branch.add(constraint))

  fun addBranch(constraint: List<BooleanFormula>): CheckData =
    this.copy(branch = branch.add(constraint))
}
