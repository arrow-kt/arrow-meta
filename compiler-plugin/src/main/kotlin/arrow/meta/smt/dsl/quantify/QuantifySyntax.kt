package arrow.meta.smt.dsl.quantify

import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.QuantifiedFormulaManager

interface QuantifySyntax {
  val quantify: QuantifiedFormulaManager

  fun forAll(vararg elements: Formula, term: () -> BooleanFormula): BooleanFormula =
    quantify.forall(elements.toMutableList(), term())

  fun exists(vararg elements: Formula, term: () -> BooleanFormula): BooleanFormula =
    quantify.exists(elements.toMutableList(), term())

  fun elimQuantifiers(term: () -> BooleanFormula): BooleanFormula =
    quantify.eliminateQuantifiers(term())
}