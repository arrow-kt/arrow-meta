package arrow.meta.phases.codegen.ir

import arrow.meta.internal.Noop
import arrow.meta.phases.CompilerContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.symbols.IrClassifierSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeAbbreviation
import org.jetbrains.kotlin.ir.types.IrTypeArgument
import org.jetbrains.kotlin.ir.types.IrTypeCheckerContext
import org.jetbrains.kotlin.ir.types.IrTypeProjection
import org.jetbrains.kotlin.ir.types.IrTypeSystemContext
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.types.impl.IrTypeBase
import org.jetbrains.kotlin.ir.types.impl.makeTypeProjection
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.ConstantValueGenerator
import org.jetbrains.kotlin.ir.util.ReferenceSymbolTable
import org.jetbrains.kotlin.ir.util.TypeTranslator
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.referenceFunction
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.asSimpleType
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class IrUtils(
  val pluginContext: IrPluginContext,
  val compilerContext: CompilerContext
) : ReferenceSymbolTable by pluginContext.symbols.externalSymbolTable,
  IrTypeSystemContext by IrTypeCheckerContext(pluginContext.irBuiltIns) {

  val typeTranslator: TypeTranslator =
    TypeTranslator(
      symbolTable = pluginContext.symbols.externalSymbolTable,
      languageVersionSettings = pluginContext.languageVersionSettings,
      builtIns = pluginContext.builtIns
    ).apply translator@{
      constantValueGenerator =
        ConstantValueGenerator(
          moduleDescriptor = pluginContext.builtIns.builtInsModule.module,
          symbolTable = pluginContext.symbols.externalSymbolTable
        ).apply {
          this.typeTranslator = this@translator
        }
    }

  fun KotlinType.toIrType(): IrType =
    typeTranslator.translateType(this)

  fun CallableDescriptor.irCall(): IrExpression =
    when (this) {
      is PropertyDescriptor -> {
        val irField = pluginContext.symbols.externalSymbolTable.referenceField(this)
        irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { irSimpleFunctionSymbol ->
          IrCallImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = irSimpleFunctionSymbol.owner.returnType,
            symbol = irSimpleFunctionSymbol,
            typeArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.typeParameters.size,
            valueArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.valueParameters.size
          )
        } ?: TODO("Unsupported irCall for $this")
      }
      is ClassConstructorDescriptor -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceConstructor(this)
        IrConstructorCallImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.returnType,
          symbol = irSymbol,
          typeArgumentsCount = irSymbol.owner.descriptor.typeParameters.size,
          valueArgumentsCount = irSymbol.owner.descriptor.valueParameters.size,
          constructorTypeArgumentsCount = irSymbol.owner.descriptor.typeParameters.size
        )
      }
      is FunctionDescriptor -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceFunction(this)
        IrCallImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.returnType,
          symbol = irSymbol,
          typeArgumentsCount = irSymbol.owner.descriptor.typeParameters.size,
          valueArgumentsCount = irSymbol.owner.descriptor.valueParameters.size
        )
      }
      is FakeCallableDescriptorForObject -> {
        val irSymbol = pluginContext.symbols.externalSymbolTable.referenceClass(classDescriptor)
        IrGetObjectValueImpl(
          startOffset = UNDEFINED_OFFSET,
          endOffset = UNDEFINED_OFFSET,
          type = irSymbol.owner.defaultType,
          symbol = irSymbol
        )
      }
      else -> {
        TODO("Unsupported ir call for $this")
      }
    }

  fun PropertyDescriptor.irGetterCall(): IrCall? {
    val irField = pluginContext.symbols.externalSymbolTable.referenceField(this)
    return irField.owner.correspondingPropertySymbol?.owner?.getter?.symbol?.let { irSimpleFunctionSymbol ->
      IrCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irSimpleFunctionSymbol.owner.returnType,
        symbol = irSimpleFunctionSymbol,
        typeArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irSimpleFunctionSymbol.owner.descriptor.valueParameters.size
      )
    }
  }

  fun ClassDescriptor.irConstructorCall(): IrConstructorCall? {
    val irClass = pluginContext.symbols.externalSymbolTable.referenceClass(this)
    return irClass.constructors.firstOrNull()?.let { irConstructorSymbol ->
      IrConstructorCallImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = irConstructorSymbol.owner.returnType,
        symbol = irConstructorSymbol,
        typeArgumentsCount = irConstructorSymbol.owner.descriptor.typeParameters.size,
        valueArgumentsCount = irConstructorSymbol.owner.descriptor.valueParameters.size,
        constructorTypeArgumentsCount = declaredTypeParameters.size
      )
    }
  }

  fun CallableDescriptor.substitutedIrTypes(typeSubstitutor: NewTypeSubstitutorByConstructorMap): List<IrType?> =
    typeParameters.mapIndexed { _, typeParamDescriptor ->
      val newType = typeSubstitutor.map.entries.find {
        it.key.toString() == typeParamDescriptor.defaultType.toString()
      }
      newType?.value?.toIrType()
    }


  fun <A> IrFunction.transform(data: A, f: IrFunction.(a: A) -> Unit = Noop.effect2): IrStatement =
    transform(object : IrElementTransformer<A> {
      override fun visitFunction(declaration: IrFunction, a: A): IrStatement {
        f(declaration, a)
        return super.visitFunction(declaration, data)
      }
    }, data)
}

fun IrCall.dfsCalls(): List<IrCall> { // search for parent function
  val calls = arrayListOf<IrCall>()
  val recursiveVisitor = object : IrElementVisitor<Unit, Unit> {
    override fun visitElement(element: IrElement, data: Unit) {
      if (element is IrCall) {
        calls.addAll(element.dfsCalls())
      }
    }
  }
  acceptChildren(recursiveVisitor, Unit)
  calls.add(this)
  return calls
}

/**
 * returns the index and the value argument
 */
val IrCall.valueArguments: List<Pair<Int, IrExpression?>>
  get() {
    val args = arrayListOf<Pair<Int, IrExpression?>>()
    for (i in 0 until valueArgumentsCount) {
      args.add(i to getValueArgument(i))
    }
    return args.toList()
  }

/**
 * returns the index and the type argument
 */
val IrCall.typeArguments: List<Pair<Int, IrType?>>
  get() {
    val args = arrayListOf<Pair<Int, IrType?>>()
    for (i in 0 until typeArgumentsCount) {
      args.add(i to getTypeArgument(i))
    }
    return args.toList()
  }

val IrCall.unsubstitutedDescriptor: FunctionDescriptor
  get() = symbol.owner.descriptor

val IrCall.substitutedValueParameters: List<Pair<ValueParameterDescriptor, KotlinType>>
  get() = unsubstitutedDescriptor.substitutedValueParameters(this)

val IrTypeParametersContainer.allTypeParameters: List<IrTypeParameter>
  get() = if (this is IrConstructor)
    parentAsClass.typeParameters + typeParameters
  else
    typeParameters

fun IrTypeParametersContainer.getTypeSubstitutionMap(expression: IrMemberAccessExpression): Map<IrTypeParameter, Either<KotlinType?, TypeProjection?>> =
  typeParameters
    .associateWith {
      // TODO: room for improvement regarding type association
      if (expression.typeArgumentsCount > it.index &&
        expression.ownersTypeParameters.any { p ->
          p.index == it.index && p == it
        })
        Either.Left(expression.getTypeArgument(it.index).safeAs<IrTypeBase>()?.kotlinType)
      else if (it.index > 0 && expression.type is IrSimpleType && it.index < (expression.type as IrSimpleType).arguments.size) {
        Either.Right(expression.type.safeAs<IrTypeBase>()?.kotlinType?.arguments?.get(it.index))
      } else {
        Either.Left(expression.type.safeAs<IrTypeBase>()?.kotlinType)
      }
    }

sealed class Either<out A, out B> {
  data class Left<out A>(val a: A) : Either<A, Nothing>()
  data class Right<out B>(val b: B) : Either<Nothing, B>()
}

val IrMemberAccessExpression.ownersTypeParameters: List<IrTypeParameter>
  get() = symbol.owner.safeAs<IrTypeParametersContainer>()?.typeParameters ?: emptyList()

/**
 * returns a Pair of the descriptor and it's substituted KotlinType at the call-site
 */
fun CallableMemberDescriptor.substitutedValueParameters(call: IrCall): List<Pair<ValueParameterDescriptor, KotlinType>> =
  valueParameters.filterNotNull()
    .map {
      val type = it.type
      it to (type.takeIf { t -> !t.isTypeParameter() }
        ?: typeParameters.filterNotNull()
          .firstOrNull { typeParam -> typeParam.defaultType == type.asSimpleType() }
          ?.let { typeParam ->
            call.getTypeArgument(typeParam.index)?.originalKotlinType
          } ?: type // Could not resolve the substituted KotlinType
        )
    }

fun IrDeclarationParent.extractTypeParameters(): List<IrTypeParameter> =
  collectSelfAndParents().mapNotNull { it.safeAs<IrTypeParametersContainer>()?.typeParameters }.flatten()

val IrMemberAccessExpression.dispatchersAndParents: Map<IrExpression?, IrDeclarationParent>
  get() = (this to symbol.owner.safeAs<IrDeclarationParent>()).collectDispatcherAndParents().toMap()

val IrMemberAccessExpression.selfAndParents: List<IrDeclarationParent>
  get() = symbol.owner.safeAs<IrDeclarationParent>().collectSelfAndParents()

val IrMemberAccessExpression.hasParent: Boolean
  get() = symbol.owner.safeAs<IrDeclarationParent>()?.parent != null

/**
 * Note: can only be used if the Ir subtree is correct.
 * If not it fails with the Ir tree error, before it resolves the type information
 */
val IrMemberAccessExpression.resolveTypeParameters: Map<IrTypeParameter, Either<KotlinType?, TypeProjection?>>
  get() {
    val result: MutableMap<IrTypeParameter, Either<KotlinType?, TypeProjection?>> = mutableMapOf()
    selfAndParents.forEach { p ->
      result.putAll(p.safeAs<IrTypeParametersContainer>()?.getTypeSubstitutionMap(this) ?: emptyMap())
    }
    return result.toMap()
  }

// TODO: look for alternative to resolve more in the tree if Dispatcher is unreachable
tailrec fun Pair<IrExpression?, IrDeclarationParent?>.collectDispatcherAndParents(
  acc: List<Pair<IrExpression?, IrDeclarationParent>> = emptyList()
): List<Pair<IrExpression?, IrDeclarationParent>> =
  if (second != null) {
    val b = second as IrDeclarationParent
    (first.safeAs<IrMemberAccessExpression>()?.dispatchReceiver to b.parent)
      .collectDispatcherAndParents(acc.plus(first to b))
  } else acc

tailrec fun IrDeclarationParent?.collectSelfAndParents(
  acc: List<IrDeclarationParent> = emptyList()
): List<IrDeclarationParent> =
  if (this != null) {
    parent.collectSelfAndParents(acc + this)
  } else {
    acc
  }

val IrDeclarationParent.parent: IrDeclarationParent?
  get() = when (this) {
    is IrField -> parent
    is IrClass -> when {
      isInner -> parent as IrClass
      visibility == Visibilities.LOCAL -> parent
      else -> null
    }
    is IrConstructor -> parent as IrClass
    is IrFunction -> if (visibility == Visibilities.LOCAL || dispatchReceiverParameter != null) {
      parent
    } else null
    else -> null
  }

/**
 * @param constructor the owner is either [IrClass] or [IrTypeParameter]
 */
fun IrSimpleType.replace(
  constructor: (IrClassifierSymbol?) -> IrClassifierSymbol? = Noop.id(),
  questionMark: Boolean = hasQuestionMark,
  args: (List<IrTypeArgument>) -> List<IrTypeArgument>? = Noop.nullable1(),
  annotation: (List<IrConstructorCall>) -> List<IrConstructorCall>? = Noop.id(),
  abbreviation: (IrTypeAbbreviation?) -> IrTypeAbbreviation? = Noop.id(),
): IrSimpleType =
  IrSimpleTypeImpl(
    constructor(classifier) ?: classifier,
    questionMark,
    args(arguments) ?: arguments,
    annotation(this.annotations) ?: annotations,
    abbreviation(this.abbreviation)
  )

/**
 * @param constructor the owner is either [IrClass] or [IrTypeParameter]
 */
fun IrSimpleType.replaceWith(
  constructor: (IrClassifierSymbol?) -> IrClassifierSymbol? = Noop.id(),
  questionMark: Boolean = hasQuestionMark,
  args: (IrTypeArgument) -> IrTypeArgument? = Noop.nullable1(),
  annotation: (IrConstructorCall) -> IrConstructorCall? = Noop.id(),
  abbreviation: (IrTypeAbbreviation?) -> IrTypeAbbreviation? = Noop.id(),
): IrSimpleType =
  replace(
    constructor = constructor,
    questionMark = questionMark,
    args = { it.map { arg -> args(arg) ?: arg } },
    annotation = { it.map { c -> annotation(c) ?: c } },
    abbreviation
  )

fun IrType.projection(variance: Variance = Variance.INVARIANT): IrTypeProjection =
  makeTypeProjection(this, variance)