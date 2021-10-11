package arrow.meta.plugins.analysis.phases.analysis.solver

import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolvedCall
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DefaultValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ResolvedValueArgument
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Expression
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type

enum class SpecialKind {
  Pre, Post, Invariant
}

internal val ResolvedCall.specialKind: SpecialKind?
  get() = when (resultingDescriptor.fqNameSafe) {
    FqName("arrow.analysis.pre") -> SpecialKind.Pre
    FqName("kotlin.require") -> SpecialKind.Pre
    FqName("arrow.analysis.post") -> SpecialKind.Post
    FqName("arrow.analysis.invariant") -> SpecialKind.Invariant
    else -> null
  }

/**
 * Returns `true` if [this] resolved call is calling [kotlin.require]
 */
internal fun ResolvedCall.isRequireCall(): Boolean =
  resultingDescriptor.fqNameSafe == FqName("kotlin.require")

/**
 * Returns `true` if the function has either
 * a dispatch or an extension receiver
 */
internal fun ResolvedCall.hasReceiver() =
  this.resultingDescriptor.dispatchReceiverParameter != null ||
    this.resultingDescriptor.extensionReceiverParameter != null

/**
 * Information about an argument in a resolved call
 */
data class ArgumentExpression(val name: String, val type: Type, val expression: Expression?)

/**
 * Get all argument expressions for [this] call
 * including extension receiver, dispatch receiver,
 * and all value arguments
 */
internal fun ResolvedCall.allArgumentExpressions(context: ResolutionContext): List<ArgumentExpression> =
  listOfNotNull((dispatchReceiver ?: extensionReceiver)?.type?.let { ArgumentExpression("this", it, getReceiverExpression()) }) +
    valueArgumentExpressions(context)

/**
 * Get all value arguments for [this] call
 */
internal fun ResolvedCall.valueArgumentExpressions(context: ResolutionContext): List<ArgumentExpression> =
  valueArguments.flatMap { (param, resolvedArg) ->
    val containingType =
      if (param.type.isTypeParameter() || param.type.isAnyOrNullableAny())
        (param.containingDeclaration?.containingDeclaration as? ClassDescriptor)?.defaultType
          ?: context.types.nothingType
      else param.type
    when {
      resolvedArg is DefaultValueArgument && resolvedArg.valueArgument == null ->
        listOfNotNull(param.defaultValue?.let { defaultValue ->
          ArgumentExpression(param.name.value, containingType, defaultValue)
        })
      else -> resolvedArg.arguments.map {
        ArgumentExpression(param.name.value, containingType, it.argumentExpression)
      }
    }
  }

/**
 * Obtains the expression from the
 * argument with a given [argumentName]
 */
internal fun ResolvedCall.arg(
  argumentName: String,
  context: ResolutionContext
): Expression? =
  allArgumentExpressions(context).find {
    it.name == argumentName
  }?.expression

/**
 * Obtains the [ResolvedValueArgument] information
 * for the argument with the given [argumentName]
 */
internal fun ResolvedCall.resolvedArg(
  argumentName: String
): ResolvedValueArgument? =
  valueArguments.firstNotNullOfOrNull { (descriptor, resolvedArg) ->
    resolvedArg.takeIf { descriptor.name.value == argumentName }
  }

/**
 * Obtains the information about the argument
 * whose expression is [arg]
 */
internal fun ResolvedCall.referencedArg(
  arg: Expression?
): Pair<ValueParameterDescriptor, ResolvedValueArgument>? =
  valueArguments.toList().firstOrNull { (_, resolvedArg) ->
    resolvedArg.arguments.any { valueArg ->
      valueArg.argumentExpression?.impl() == arg?.impl()
    }
  }
