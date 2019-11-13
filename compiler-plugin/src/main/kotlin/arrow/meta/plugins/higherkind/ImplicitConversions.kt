package arrow.meta.plugins.higherkind

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.inference.substituteAndApproximateCapturedTypes
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeApproximator
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.isNullable
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.builtIns
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.kotlin.types.typeUtil.substitute

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {

      val proofs: HashMap<Pair<KotlinType, KotlinType>, FunctionDescriptor> = hashMapOf()

      fun conversionDescriptor(subType: KotlinType, superType: KotlinType): FunctionDescriptor? =
        proofs.entries.find { (types, conversion) ->
          val (from, to) = types
          val fromSub = if (from.isTypeParameter()) from.constructor to subType.makeNotNullable().unwrap() else null
          val toSub = if (to.isTypeParameter()) to.constructor to superType.makeNotNullable().unwrap() else null
          val appliedConversion = conversion.substituteAndApproximateCapturedTypes(
            NewTypeSubstitutorByConstructorMap(listOfNotNull(fromSub, toSub).toMap()),
            TypeApproximator(conversion.module.builtIns)
          )
          appliedConversion.extensionReceiverParameter?.type?.let { receiver ->
            appliedConversion.returnType?.let { returnType ->
              NewKotlinTypeChecker.run {
                isSubtypeOf(receiver, subType) && isSubtypeOf(returnType, superType)
              }
            }
          } == true
        }?.value

      fun KotlinType.applyProof(superType: KotlinType): Boolean =
        conversionDescriptor(this, superType) != null

      fun IrUtils.initializeConversion(conversion: IrMemberAccessExpression, argument: IrExpression?): Unit {
        conversion.apply {
          extensionReceiver = argument
          (0 until typeArgumentsCount).forEach {
            putTypeArgument(it, backendContext.irBuiltIns.anyNType) //TODO properly apply the conversion
          }
        }
      }

      fun IrUtils.insertConversion(it: IrVariable): IrVariable? {
        val targetType = it.type.originalKotlinType
        val valueType = it.initializer?.type?.originalKotlinType
        return if (targetType != null && valueType != null) { //insert conversion
          it.apply {
            initializer = conversionDescriptor(valueType, targetType)?.irCall()?.apply {
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
              conversionDescriptor(valueType, targetType)?.irCall()?.apply {
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
            p0.applyProof(p1) //TODO this still yields wrong results, change entire lookup to typeconversions to be descriptor based assuming descriptors are precompiled so types can be compared with the function descriptor, try typeclasses as single argument encoding + intersection on resolution
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
          conversionDescriptor(valueType, targetType)?.irCall()?.let { call ->
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













