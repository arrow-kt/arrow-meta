package arrow.meta.plugins.liquid.phases.analysis.solver.state

import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.elements.BinaryExpression
import arrow.meta.plugins.liquid.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.liquid.smt.Solver
import org.sosy_lab.java_smt.api.BooleanFormula
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.NumeralFormula

// TODO: remove when we can obtain the laws
internal fun Solver.specialCasingForResolvedCalls(
  resolvedCall: ResolvedCall,
): ((result: Formula, arg1: Formula, arg2: Formula) -> BooleanFormula?)? =
  ints {
    booleans {
      when (resolvedCall.resultingDescriptor.fqNameSafe) {
        FqName("kotlin.Int.equals") -> { result, arg1, arg2 ->
          equivalence(
            result as BooleanFormula,
            equal(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
          )
        }
        FqName("kotlin.Int.plus") -> { result, arg1, arg2 ->
          equal(
            result as NumeralFormula.IntegerFormula,
            add(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
          )
        }
        FqName("kotlin.Int.minus") -> { result, arg1, arg2 ->
          equal(
            result as NumeralFormula.IntegerFormula,
            subtract(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
          )
        }
        FqName("kotlin.Int.times") -> { result, arg1, arg2 ->
          equal(
            result as NumeralFormula.IntegerFormula,
            multiply(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
          )
        }
//        FqName("kotlin.Int.div") -> { result, arg1, arg2 ->
//          equal(result as NumeralFormula.IntegerFormula, divide(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula))
//        }
        FqName("kotlin.Int.compareTo") -> {
          when ((resolvedCall.callElement as? BinaryExpression)?.operationToken) {
            ">" -> { result, arg1, arg2 ->
              equivalence(
                result as BooleanFormula,
                greaterThan(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
              )
            }
            ">=" -> { result, arg1, arg2 ->
              equivalence(
                result as BooleanFormula,
                greaterOrEquals(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
              )
            }
            "<" -> { result, arg1, arg2 ->
              equivalence(
                result as BooleanFormula,
                lessThan(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
              )
            }
            "<=" -> { result, arg1, arg2 ->
              equivalence(
                result as BooleanFormula,
                lessOrEquals(arg1 as NumeralFormula.IntegerFormula, arg2 as NumeralFormula.IntegerFormula)
              )
            }
            else -> null
          }
        }
        else -> null
      }
    }
  }
