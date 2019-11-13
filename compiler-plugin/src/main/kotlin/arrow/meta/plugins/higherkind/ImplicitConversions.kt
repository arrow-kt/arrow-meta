package arrow.meta.plugins.higherkind

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
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
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeApproximator
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {

      val proofs: HashMap<Pair<KotlinType, KotlinType>, FunctionDescriptor> = hashMapOf()

      fun conversionDescriptor(subType: KotlinType, superType: KotlinType): FunctionDescriptor? {
        return proofs.entries.firstOrNull { (types, conversion) ->
          val (from, to) = types
          val fromSub = if (from.isTypeParameter()) from.constructor to subType.makeNotNullable().unwrap() else null
          val toSub = if (to.isTypeParameter()) to.constructor to superType.makeNotNullable().unwrap() else null
          val appliedConversion = conversion.substituteAndApproximateCapturedTypes(
            NewTypeSubstitutorByConstructorMap(listOfNotNull(fromSub, toSub).toMap()),
            TypeApproximator(conversion.module.builtIns)
          )
          val result = appliedConversion.extensionReceiverParameter?.type?.let { receiver ->
            appliedConversion.returnType?.let { returnType ->
              NewKotlinTypeChecker.run {
                isSubtypeOf(receiver, subType) && isSubtypeOf(returnType, superType)
              }
            }
          } ?: false
          result
        }?.value
      }

      fun IrUtils.conversionDescriptorCall(subType: KotlinType, superType: KotlinType): IrCall? {
        var selected: FunctionDescriptor? = null
        val mappedTypeArgs: HashMap<KotlinType, KotlinType> = hashMapOf()
        val unwrappedSubType = subType.makeNotNullable().unwrap()
        val unwrappedSuperType = superType.makeNotNullable().unwrap()
        return proofs.entries.firstOrNull { (types, conversion) ->
          val (from, to) = types
          val fromSub = if (from.isTypeParameter()) from.constructor to unwrappedSubType else null
          val toSub = if (to.isTypeParameter()) to.constructor to unwrappedSuperType else null
          val appliedConversion = conversion.substituteAndApproximateCapturedTypes(
            NewTypeSubstitutorByConstructorMap(listOfNotNull(fromSub, toSub).toMap()),
            TypeApproximator(conversion.module.builtIns)
          )
          val result = appliedConversion.extensionReceiverParameter?.type?.let { receiver ->
            appliedConversion.returnType?.let { returnType ->
              NewKotlinTypeChecker.run {
                isSubtypeOf(receiver, subType) && isSubtypeOf(returnType, superType)
              }
            }
          } ?: false
          if (result) {
            if (from.isTypeParameter()) mappedTypeArgs[from] = unwrappedSubType
            if (to.isTypeParameter()) mappedTypeArgs[to] = unwrappedSuperType
            selected = appliedConversion as FunctionDescriptor
            return@firstOrNull result
          }
          result
        }?.value?.irCall()?.also { result ->
          selected?.let { fn ->
            mappedTypeArgs.entries.forEachIndexed { n, entry ->
              val type = typeTranslator.translateType(entry.value)
              result.putTypeArgument(n, type)
            }
          }
        }
      }

      fun applyProof(subType: KotlinType, superType: KotlinType): Boolean =
        conversionDescriptor(subType, superType) != null

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

      class ImplicitConversionsTypeChecker(val default: KotlinTypeChecker) : KotlinTypeChecker {
        override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean {
          val result = default.isSubtypeOf(p0, p1)
          val subTypes = if (!result) {
            applyProof(p0, p1)
          } else result
          println("typeConversion:isSubtypeOf: $p0 : $p1 -> $subTypes")
          return subTypes
        }

        override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean {
          val result = default.equalTypes(p0, p1)
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
        typeChecker(::ImplicitConversionsTypeChecker),
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










