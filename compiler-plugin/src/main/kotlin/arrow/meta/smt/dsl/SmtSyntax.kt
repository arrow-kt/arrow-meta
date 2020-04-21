package arrow.meta.smt.dsl

import arrow.meta.smt.dsl.solver.SolverAlgebra
import arrow.meta.smt.dsl.solver.boolector.BoolectorScope
import arrow.meta.smt.dsl.solver.cvc4.Cvc4Scope
import arrow.meta.smt.dsl.solver.mathsat5.Mathsat5Scope
import arrow.meta.smt.dsl.solver.princess.PrincessScope
import arrow.meta.smt.dsl.solver.smtinterpol.SmtInterpolScope
import arrow.meta.smt.dsl.solver.z3.Z3Scope

interface SmtSyntax {
  fun <A> Princess(name: String, smt: PrincessScope.() -> A): SolverAlgebra.Princess<A> =
    SolverAlgebra.Princess(name, smt)

  fun <A> Z3(name: String, smt: Z3Scope.() -> A): SolverAlgebra.Z3<A> =
    SolverAlgebra.Z3(name, smt)

  fun <A> Cvc4(name: String, smt: Cvc4Scope.() -> A): SolverAlgebra.Cvc4<A> =
    SolverAlgebra.Cvc4(name, smt)

  fun <A> Mathsat5(name: String, smt: Mathsat5Scope.() -> A): SolverAlgebra.Mathsat5<A> =
    SolverAlgebra.Mathsat5(name, smt)

  fun <A> SmtInterpol(name: String, smt: SmtInterpolScope.() -> A): SolverAlgebra.SmtInterpol<A> =
    SolverAlgebra.SmtInterpol(name, smt)

  fun <A> Boolector(name: String, smt: BoolectorScope.() -> A): SolverAlgebra.Boolector<A> =
    SolverAlgebra.Boolector(name, smt)
}