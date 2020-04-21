package arrow.meta.smt.dsl.solver

import arrow.meta.smt.dsl.solver.boolector.BoolectorScope
import arrow.meta.smt.dsl.solver.cvc4.Cvc4Scope
import arrow.meta.smt.dsl.solver.mathsat5.Mathsat5Scope
import arrow.meta.smt.dsl.solver.princess.PrincessScope
import arrow.meta.smt.dsl.solver.smtinterpol.SmtInterpolScope
import arrow.meta.smt.dsl.solver.z3.Z3Scope
import org.sosy_lab.common.ShutdownNotifier
import org.sosy_lab.common.log.LogManager

sealed class SolverAlgebra {
  data class Princess<A>(
    val name: String,
    val smt: PrincessScope.() -> A
  ) : SolverAlgebra()

  data class Z3<A>(
    val name: String,
    val smt: Z3Scope.() -> A
  ) : SolverAlgebra()

  data class Cvc4<A>(
    val name: String,
    val smt: Cvc4Scope.() -> A
  ) : SolverAlgebra()

  data class Mathsat5<A>(
    val name: String,
    val smt: Mathsat5Scope.() -> A
  ) : SolverAlgebra()

  data class SmtInterpol<A>(
    val name: String,
    val smt: SmtInterpolScope.() -> A
  ) : SolverAlgebra()

  data class Boolector<A>(
    val name: String,
    val smt: BoolectorScope.() -> A
  ) : SolverAlgebra()
}