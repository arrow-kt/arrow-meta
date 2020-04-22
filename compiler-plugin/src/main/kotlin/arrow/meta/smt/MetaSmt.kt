package arrow.meta.smt

import arrow.meta.smt.dsl.SmtSyntax
import arrow.meta.smt.dsl.scope.int.Int
import arrow.meta.smt.dsl.solver.SolverAlgebra
import arrow.meta.smt.internal.registry.SmtInternalRegistry
import org.sosy_lab.common.ShutdownNotifier
import org.sosy_lab.common.configuration.Configuration
import org.sosy_lab.common.log.LogManager
import org.sosy_lab.java_smt.api.SolverContext

class MetaSmt : SmtSyntax, SmtInternalRegistry {
  override fun resolve(conf: Configuration, log: LogManager, notifier: ShutdownNotifier): SolverAlgebra =
    Princess("Hello World") {
      val (a: Int, b: Int, c: Int) = variables("a", "b", "c")
      val plusAssoc = forAll(a, b) {
        a + b eq b + a % c
      }
      val parsed1 = parse(test2.lines().first())
      val prover = ctx.newProverEnvironment(SolverContext.ProverOptions.GENERATE_MODELS)
      prover.addConstraint(parsed1)
      if (!prover.isUnsat) {
        println("Model assginments${prover.modelAssignments}")
        val bb = prover.model.toList()
        println(bb)
      } else {
        prover
        println("Unsolved Model")
      }
      prover.close()
    }
}

//val contraint = //bool.or(
//  int.equal(int.divide(a, int.makeNumber(0)), c)
// int.equal(int.add(a, c), int.multiply(int.makeNumber(2), b))

fun main() {
  //println(ctx(SolverContextFactory.Solvers.Z3).version)
  MetaSmt().registerSolver()
}


val test =
  """
(declare-datatypes () ((Nat (Z) (S (p Nat)))))
(declare-datatypes () ((list (nil) (cons (head Nat) (tail list)))))
(declare-fun plus (Nat Nat) Nat)
(declare-fun equal (Nat Nat) Bool)
(declare-fun count (Nat list) Nat)
(declare-fun append (list list) list)
(assert
  (not
    (forall ((n Nat) (xs list) (ys list))
      (= (plus (count n xs) (count n ys)) (count n (append xs ys))))))
(assert
  (forall ((x Nat) (y Nat))
    (= (plus x y) (ite (is-S x) (S (plus (p x) y)) y))))
(assert
  (forall ((x Nat) (y Nat))
    (= (equal x y)
      (ite
        (is-S x) (ite (is-S y) (equal (p x) (p y)) false)
        (not (is-S y))))))
(assert
  (forall ((x Nat) (y list))
    (= (count x y)
      (ite
        (is-cons y)
        (ite (equal x (head y)) (S (count x (tail y))) (count x (tail y)))
        Z))))
(assert
  (forall ((x list) (y list))
    (= (append x y)
      (ite (is-cons x) (cons (head x) (append (tail x) y)) y))))
  """.trimIndent()

val test2 =
  """
(declare-const x_0 (_ BitVec 32))
(declare-const x_1 (_ BitVec 32))
(declare-const x_2 (_ BitVec 32))   
(declare-const y_0 (_ BitVec 32))
(declare-const y_1 (_ BitVec 32))   
(assert (= x_1 (bvadd x_0 y_0))) 
(assert (= y_1 (bvsub x_1 y_0)))
(assert (= x_2 (bvsub x_1 y_1)))
(assert (not
  (and (= x_2 y_0)
       (= y_1 x_0))))
  """.trimIndent()