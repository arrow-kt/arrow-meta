package arrow.meta.plugins.liquid.phases.analysis.solver.check.model

import arrow.meta.continuations.ContSeq
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import org.sosy_lab.java_smt.api.BooleanFormula

data class CurrentVarInfo(val varInfo: MutableList<VarInfo>) {

  fun get(name: String): VarInfo? =
    varInfo.firstOrNull { it.name == name }

  fun get(name: FqName): VarInfo? =
    this.get(name.asString())

  fun add(name: String, smtName: String, origin: KtElement, invariant: BooleanFormula?) {
    varInfo.add(0, VarInfo(name, smtName, origin, invariant))
  }

  fun bracket(): ContSeq<Unit> = ContSeq {
    val currentVarInfo = varInfo.toList()
    yield(Unit)
    varInfo.clear()
    varInfo.addAll(currentVarInfo)
  }
}
