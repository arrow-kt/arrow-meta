package arrow.meta.plugins.liquid.phases.solver.collector

import arrow.meta.plugins.liquid.phases.solver.Solver
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.fir.builder.toFirOperation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.psi.expressionRecursiveVisitor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getReceiverExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isAnyOrNullableAny
import org.jetbrains.kotlin.types.typeUtil.isBoolean
import org.jetbrains.kotlin.types.typeUtil.isInt
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.sosy_lab.java_smt.api.Formula
import org.sosy_lab.java_smt.api.FormulaType

data class DeclarationConstraints(
  val descriptor: DeclarationDescriptor,
  val element: KtDeclaration,
  val pre: List<Formula>,
  val post: List<Formula>
)

internal fun ResolvedCall<out CallableDescriptor>.preCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.pre")

internal fun ResolvedCall<out CallableDescriptor>.postCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("arrow.refinement.post")

internal fun ResolvedCall<out CallableDescriptor>.preOrPostCall(): Boolean =
  preCall() || postCall()

fun Solver.formula(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  bindingContext: BindingContext,
): Pair<ResolvedCall<out CallableDescriptor>, Formula>? =
  ints {
    val callable = resolvedCall.resultingDescriptor
    val call = callable.builtIns.run {
      call(resolvedCall, bindingContext)
    }
    call?.let { resolvedCall to it }
  }

private fun Solver.call(
  resolvedCall: ResolvedCall<out CallableDescriptor>,
  bindingContext: BindingContext,
): Formula? {
  val args = argsFormulae(bindingContext, resolvedCall.call.callElement)
  val descriptor = resolvedCall.resultingDescriptor
  return formulaWithArgs(descriptor, args, resolvedCall)
}

private fun Solver.formulaWithArgs(
  descriptor: CallableDescriptor,
  args: List<Formula>,
  resolvedCall: ResolvedCall<out CallableDescriptor>
): Formula? = when (descriptor.fqNameSafe) {
  FqName("arrow.refinement.pre") -> {
    //recursion ends here
    args[0] //TODO apparently we don't get called for composed predicates with && or ||
  }
  FqName("arrow.refinement.post") -> {
    //recursion ends here
    args[0] //TODO apparently we don't get called for composed predicates with && or ||
  }
  FqName("kotlin.Int.equals") -> intEquals(args)
  FqName("kotlin.Int.plus") -> intPlus(args)
  FqName("kotlin.Int.minus") -> intMinus(args)
  FqName("kotlin.Int.times") -> intMultiply(args)
  FqName("kotlin.Int.div") -> intDivide(args)
  FqName("kotlin.Int.compareTo") -> {
    val op = (resolvedCall.call.callElement as? KtBinaryExpression)?.operationToken?.toFirOperation()?.operator
    when (op) {
      ">" -> intGreaterThan(args)
      ">=" -> intGreaterThanOrEquals(args)
      "<" -> intLessThan(args)
      "<=" -> intLessThanOrEquals(args)
      else -> null
    }
  }
  else -> null
}

private fun Solver.argsFormulae(
  bindingContext: BindingContext,
  element: KtElement
): List<Formula> {
  val results = arrayListOf<Formula>()
  val visitor = expressionRecursiveVisitor {
    val resolvedCall = it.getResolvedCall(bindingContext)
    if (resolvedCall != null && !resolvedCall.preOrPostCall()) { // not match on the parent call
      val args = argsFormulae(resolvedCall)
      val descriptor = resolvedCall.resultingDescriptor
      val expressionFormula = formulaWithArgs(descriptor, args.map { it.third }, resolvedCall)
      expressionFormula?.also { results.add(it) }
    }
  }
  element.accept(visitor)
  return results.distinct()
}

internal fun <D : CallableDescriptor> Solver.argsFormulae(
  resolvedCall: ResolvedCall<D>
): List<Triple<KotlinType, String, Formula>> =
  ints {
    booleans {
      val argsExpressions = resolvedCall.allArgumentExpressions()
      argsExpressions.mapNotNull { (name, type, maybeEx) ->
        when (val ex = maybeEx) {
          is KtConstantExpression ->
            when {
              type.isInt() ->
                Triple(type, name, makeNumber(ex.text))
              type.isBoolean() ->
                Triple(type, name , makeBoolean(ex.text.toBooleanStrict()))
              else -> null
            }
          else -> ex?.text?.let {
            when {
              type.isInt() ->
                Triple(type, name, makeVariable(FormulaType.IntegerType, ex.text))
              type.isBoolean() ->
                Triple(type, name, makeVariable(FormulaType.BooleanType, ex.text))
              else -> null
            }
          }
        }
      }
    }
  }

private fun <D : CallableDescriptor> ResolvedCall<D>.allArgumentExpressions(): List<Triple<String, KotlinType, KtExpression?>> =
  listOfNotNull((dispatchReceiver ?: extensionReceiver)?.type?.let { Triple("this", it, getReceiverExpression()) }) +
    valueArguments.flatMap { (param, resolvedArg) ->
      val containingType =
        if (param.type.isTypeParameter() || param.type.isAnyOrNullableAny())
          (param.containingDeclaration.containingDeclaration as? ClassDescriptor)?.defaultType
            ?: param.builtIns.nothingType
        else param.type
      resolvedArg.arguments.mapIndexed { n, it ->
        Triple(param.name.asString(), containingType, it.getArgumentExpression())
      }
    }


fun KotlinType.formulaType(): FormulaType<*>? =
  when {
    isInt() -> FormulaType.IntegerType
    isBoolean() -> FormulaType.BooleanType
    else -> null
  }


private fun KtElement.constraintsDSLElements(): Set<PsiElement> {
  val results = hashSetOf<PsiElement>()
  val visitor = callExpressionRecursiveVisitor {
    if (it.calleeExpression?.text == "pre" || it.calleeExpression?.text == "post") {
      results.add(it)
    }
  }
  accept(visitor)
  acceptChildren(visitor)
  return results
}

internal fun KtDeclaration.constraints(
  solver: Solver,
  context: BindingContext
): List<Pair<ResolvedCall<*>, Formula>> =
  constraintsDSLElements().filterIsInstance<KtElement>().mapNotNull {
    val call = it.getResolvedCall(context)
    if (call != null && call.preOrPostCall()) {
      val f = solver.formula(call, context)
      f
    } else null
  }