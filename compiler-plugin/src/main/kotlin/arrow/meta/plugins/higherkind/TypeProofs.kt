package arrow.meta.plugins.higherkind

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.codegen.coroutines.createCustomCopy
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.FunctionDescriptorImpl
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.inference.substituteAndApproximateCapturedTypes
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.FlexibleType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.types.TypeApproximator
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeSubstitution
import org.jetbrains.kotlin.types.TypeSubstitutor
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.inheritEnhancement
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound
import org.jetbrains.kotlin.types.typeUtil.substitute

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {

      val proofs: HashMap<Pair<KotlinType, KotlinType>, FunctionDescriptor> = hashMapOf()

//      @Suppress("UNREACHABLE_CODE")
//      fun FunctionDescriptor.applyProof2(
//        session: ResolveSession,
//        subType: KotlinType,
//        superType: KotlinType
//      ): OverloadResolutionResults<FunctionDescriptor> {
//        val call: Call = TODO() //how to get this
//        val extensionReceiver = ExtensionReceiver(this, subType, null)
//        val newCall = ResolvedCallImpl(
//          call,
//          this,
//          null, extensionReceiver, ExplicitReceiverKind.EXTENSION_RECEIVER,
//          null, DelegatingBindingTrace(BindingTraceContext().bindingContext, "Temporary trace for unwrapped suspension function"),
//          TracingStrategy.EMPTY, MutableDataFlowInfoForArguments.WithoutArgumentsCheck(DataFlowInfo.EMPTY)
//        )
//        val functionScope = session.declarationScopeProvider.getResolutionScopeForDeclaration(call) // TODO get the PSI
//        val innerScope = FunctionDescriptorUtil.getFunctionInnerScope(functionScope, this, session.trace, OverloadChecker(TypeSpecificityComparator.NONE))
//        return componentProvider.get<PSICallResolver>().runResolutionAndInferenceForGivenCandidates(
//          context = BasicCallResolutionContext.create(
//            ... ///TODO not sure how to bring these instances to life here
//        )
//        resolutionCandidates = proofs.values.map { ResolutionCandidate.create(call, it) },
//        tracingStrategy = TracingStrategy.EMPTY
//        )
//      }

      fun KotlinType.typeArgumentsToUpperBounds2(): KotlinType =
        replace(arguments.map { typeProjection ->
          typeProjection.substitute {
            TypeUtils.getTypeParameterDescriptorOrNull(it)?.representativeUpperBound ?: it
          }
        })

      fun CallableDescriptor.applicableTo(
        from: KotlinType,
        to: KotlinType
      ): Boolean =
        extensionReceiverParameter?.type?.let { receiver ->
          returnType?.let { returnType ->
            //TODO this is duplicated with the typechecking code below and subtype, supertype may be in the wrong positions when typechecking vs conversion
            val receiverWithUpperBounds = receiver.typeArgumentsToUpperBounds2()
            val returnTypeWithUpperBounds = returnType.typeArgumentsToUpperBounds2()
            val result = NewKotlinTypeChecker.run {
              isSubtypeOf(from, receiverWithUpperBounds) && isSubtypeOf(to, returnTypeWithUpperBounds)
            }
            result
          }
        } ?: false

      fun KotlinType.typeArgMap(other: KotlinType): Map<TypeProjection, TypeProjection> =
        arguments.mapIndexed { n, typeProjection ->
          other.arguments.getOrNull(n)?.let {
            typeProjection to it
          }
        }.filterNotNull().toMap()

      tailrec fun List<KotlinType>.nestedTypeArguments(acc: List<KotlinType> = emptyList()): List<KotlinType> =
        when {
          isEmpty() -> acc
          else -> {
            val head = get(0)
            val tail = drop(1)
            val newAcc = if (head.isTypeParameter()) acc + head else acc
            val newTail = tail + head.arguments.map { it.type }
            newTail.nestedTypeArguments(newAcc)
          }
        }

      fun KotlinType.nestedTypeArguments(): List<KotlinType> =
        listOf(this).nestedTypeArguments()

      fun applyProof(subType: KotlinType, superType: KotlinType): Boolean {
        val unwrappedSubType = subType.makeNotNullable().unwrap()
        val unwrappedSuperType = superType.makeNotNullable().unwrap()
        val result = proofs.entries.any { (types, conversion) ->
          val (from, to) = types
          val fromSub = if (from.isTypeParameter()) from.constructor to subType.makeNotNullable().unwrap() else null
          val toSub = if (to.isTypeParameter()) to.constructor to superType.makeNotNullable().unwrap() else null
          val appliedConversion = conversion.substituteAndApproximateCapturedTypes(
            NewTypeSubstitutorByConstructorMap(listOfNotNull(fromSub, toSub).toMap()),
            TypeApproximator(conversion.module.builtIns)
          )
          val result = appliedConversion.applicableTo(subType, superType)
          result
        }
        return result
      }

      //Kind22<Id(_), Int, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>?
      //Kind22<Id(_), Any?, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing, Nothing>
      fun IrUtils.conversionDescriptorCall(subType: KotlinType, superType: KotlinType): IrCall? {
        var selected: FunctionDescriptor? = null
        val typeSubstitutor: HashMap<TypeConstructor, UnwrappedType> = hashMapOf()
        val unwrappedSubType = subType.makeNotNullable().unwrap()
        val unwrappedSuperType = superType.makeNotNullable().unwrap()
        return proofs.entries.firstOrNull { (types, conversion) ->
          val (from, to) = types
          val fromArgsMap = from.typeArgMap(unwrappedSubType)
          val toArgsMap = to.typeArgMap(unwrappedSuperType)
          val allArgsMap = fromArgsMap.filter { it.key.type.isTypeParameter() } + toArgsMap.filter { it.key.type.isTypeParameter() }
          typeSubstitutor.clear()
          typeSubstitutor += allArgsMap.map { it.key.type.constructor to it.value.type.unwrap() }
          val appliedConversion = conversion.substituteAndApproximateCapturedTypes(NewTypeSubstitutorByConstructorMap(typeSubstitutor), TypeApproximator(conversion.module.builtIns))
          val result = appliedConversion.applicableTo(subType, superType)
          if (result) {
            selected = appliedConversion as FunctionDescriptor
          }
          result
        }?.value?.run {
          selected?.let { fn ->
            val irTypes = fn.typeParameters.mapIndexed { n, typeParamDescriptor ->
              val newType = typeSubstitutor.entries.find {
                it.key.toString() == typeParamDescriptor.defaultType.toString()
              }
              newType?.let { typeTranslator.translateType(it.value) }
            }
            newCopyBuilder().build()?.irCall()?.apply {
              irTypes.forEachIndexed { n, type ->
                putTypeArgument(n, type)
              }
            }
          }
        }
      }

      fun initializeConversion(conversion: IrMemberAccessExpression, argument: IrExpression?): Unit {
        conversion.apply {
          extensionReceiver = argument
//          (0 until typeArgumentsCount).forEach {
//            putTypeArgument(it, backendContext.irBuiltIns.anyNType) //TODO properly apply the conversion with the same substitution we do to find a match, nullable and reified type may be failing because we set the the type args to any? here and the reifid type is seen as java lang object
//            //TODO try just inserting a global smartcast for all the from -> to and replace smat casts type operators in iR
//          }
        }
      }

      fun IrUtils.insertConversion(it: IrVariable): IrVariable? {
        val targetType = it.type.originalKotlinType
        val valueType = it.initializer?.type?.originalKotlinType
        return if (targetType != null && valueType != null) { //insert conversion
          it.apply {
            initializer = conversionDescriptorCall(valueType, targetType)?.apply {
              initializeConversion(this, initializer)
              //putValueArgument(0, initializer)
            } ?: initializer
          }
        } else it
      }


      fun IrUtils.insertConversion(it: IrProperty): IrProperty? {
        val targetType = it.descriptor.returnType
        val valueType = it.backingField?.initializer?.expression?.type?.originalKotlinType
        return if (targetType != null && valueType != null) { //insert conversion
          it.backingField?.let { field ->
            val replacement = field.initializer?.expression?.let {
              conversionDescriptorCall(valueType, targetType)?.apply {
                initializeConversion(this, it)
              }
            }
            replacement?.let { field.initializer?.expression = it }
            it
          }
        } else it
      }

      class ImplicitConversionsTypeChecker : KotlinTypeChecker {
        override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
          val result = NewKotlinTypeChecker.isSubtypeOf(p0, p1)
          val subTypes = if (!result) {
            applyProof(p0, p1)
          } else result
          println("typeConversion:isSubtypeOf: $p0 : $p1 -> $subTypes")
          return subTypes
        }

        override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
          val result = NewKotlinTypeChecker.equalTypes(p0, p1)
          println("typeConversion:equalTypes: $p0 : $p1 -> $result")
          return result
        }
      }

      fun IrUtils.insertConversion(it: IrReturn): IrReturn? {
        val targetType = it.returnTarget.returnType
        val valueType = it.value.type.originalKotlinType
        return if (targetType != null && valueType != null) { //insert conversion
          conversionDescriptorCall(valueType, targetType)?.let { call ->
            initializeConversion(call, it.value)
            //call.putValueArgument(0, it.value)
            IrReturnImpl(
              UNDEFINED_OFFSET,
              UNDEFINED_OFFSET,
              typeTranslator.translateType(targetType),
              it.returnTargetSymbol,
              call
            )
          } ?: it
        } else it
      }

      meta(
        enableIr(),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            module.fetchTypeProofs(proofs)
            println("Found type conversion proofs:\n${proofs.toList().joinToString("\n") { (path, through) ->
              val (from, to) = path
              "$from -> [${through.fqNameSafe}] -> $to:"
            }}")
            null
          },
          analysisCompleted = { project, module, bindingTrace, files ->
            null
          }
        ),
        typeChecker { ImplicitConversionsTypeChecker() },
        irVariable(IrUtils::insertConversion),
        irProperty(IrUtils::insertConversion),
        irReturn(IrUtils::insertConversion),
        irDump()
      )
    }

private fun ModuleDescriptor.fetchTypeProofs(typeConversions: HashMap<Pair<KotlinType, KotlinType>, FunctionDescriptor>): Unit {
  (getSubPackagesOf(FqName.ROOT) { true } + FqName.ROOT).flatMap { packageName ->
    getPackage(packageName).fragments.flatMap { packageFragmentDescriptor ->
      println("Scanning package: ${packageFragmentDescriptor.fqName}")
      packageFragmentDescriptor.getMemberScope().getContributedDescriptors { true }.filterIsInstance<FunctionDescriptor>().filter {
        it.annotations.hasAnnotation(ArrowProof)
      }.map { fn ->
        println("Found proof: ${fn.name}")
        fn.extensionReceiverParameter?.type?.let { from ->
          fn.returnType?.let { to ->
            typeConversions[from to to] = fn
          }
        }

      }
    }
  }
}

private val ArrowProof: FqName =
  FqName("arrow.proof")

private object ForceTypeCopySubstitution : TypeSubstitution() {
  override fun get(key: KotlinType) =
    with(key) {
      if (isError) return@with asTypeProjection()
      KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(annotations, constructor, arguments, isMarkedNullable, memberScope).asTypeProjection()
    }

  override fun isEmpty() = false
}








