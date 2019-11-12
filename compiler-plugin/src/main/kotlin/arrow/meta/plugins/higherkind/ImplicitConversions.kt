package arrow.meta.plugins.higherkind

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.phases.codegen.ir.IrUtils
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrReturnImpl
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.typeUtil.replaceArgumentsWithStarProjections


val typeProofAnnotation: Regex = Regex("@(arrow\\.)?typeProof.*")

internal class TypeConversion(
  val from: KtTypeReference,
  val to: KtTypeReference,
  val conversion: KtNamedFunction,
  val implicit: Boolean
) {
  override fun toString(): String =
    "TypeConversion(${from.text} -> ${conversion.name} -> ${to.text}, implicit = $implicit)"
}

private fun KtNamedFunction.isImplicitConversion(): Boolean =
  annotationEntries.any { entry ->
    entry.valueArguments.any {
      it.getArgumentName()?.asName?.asString() == "implicitConversion" &&
        it.getArgumentExpression()?.text == "true"
    }
  }

val Meta.implicitConversion: Plugin
  get() =
    "implicitConversion" {

      val typeConversions: HashSet<TypeConversion> = hashSetOf()

      fun TypeConversion.isTypeArgument(ktTypeReference: KtTypeReference): Boolean =
        conversion.typeParameters.any { it.name == ktTypeReference.text }

      fun TypeConversion.typeArgsMatchReferenceTypeArgs(kotlinType: KotlinType, ktTypeReference: KtTypeReference): Boolean =
        ktTypeReference.typeElement?.typeArgumentsAsTypes?.zip(kotlinType.arguments)?.all { (typeRef, typeProjection) ->
          isTypeArgument(ktTypeReference) || kotlinType.toString() == ktTypeReference.text
        } == true

      fun TypeConversion.matches(kotlinType: KotlinType, ktTypeReference: KtTypeReference): Boolean =
        isTypeArgument(ktTypeReference) ||
          typeArgsMatchReferenceTypeArgs(kotlinType, ktTypeReference) ||
          kotlinType.toString() == ktTypeReference.text

      fun KotlinType.conversion(to: KotlinType): TypeConversion? =
        typeConversions.find { it.matches(this, it.from) && it.matches(to, it.to) }

      fun TypeConversion.applyImplicitConversion(from: KotlinType, to: KotlinType): Boolean =
        matches(from, this.from) && matches(to, this.to)

      fun applyImplicitConversion(subType: KotlinType, superType: KotlinType): Boolean =
        subType != superType && typeConversions.any { it.applyImplicitConversion(subType, superType) }

      fun IrUtils.conversionDescriptor(subType: KotlinType, superType: KotlinType): FunctionDescriptor? {
        val typeConversion: TypeConversion? = subType.conversion(superType)
        return backendContext.ir.irModule.descriptor
          .getPackage(FqName.ROOT)
          .memberScope.getContributedDescriptors(DescriptorKindFilter.FUNCTIONS) {
          it == typeConversion?.conversion?.nameAsSafeName
        }.firstOrNull() as? FunctionDescriptor
      }

      fun IrUtils.insertConversion(it: IrVariable): IrVariable? {
        val targetType = it.type.originalKotlinType
        val valueType = it.initializer?.type?.originalKotlinType
        return if (targetType != null && valueType != null && applyImplicitConversion(valueType, targetType)) { //insert conversion
          it.apply {
            initializer = conversionDescriptor(valueType, targetType)?.irCall()?.apply {
              extensionReceiver = initializer
              //putValueArgument(0, initializer)
            }
          }
        } else it
      }

      fun IrUtils.insertConversion(it: IrProperty): IrProperty? {
        val targetType = it.descriptor.returnType
        val valueType = it.backingField?.initializer?.expression?.type?.originalKotlinType
        return if (targetType != null && valueType != null && applyImplicitConversion(valueType, targetType)) { //insert conversion
          it.backingField?.let { field ->
            val replacement = field.initializer?.expression?.let {
              conversionDescriptor(valueType, targetType)?.irCall()?.apply {
                extensionReceiver = it
                //putValueArgument(0, it)
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
            applyImplicitConversion(p0, p1)
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
        return if (targetType != null && valueType != null && applyImplicitConversion(valueType, targetType)) { //insert conversion
          conversionDescriptor(valueType, targetType)?.irCall()?.let { call ->
            call.extensionReceiver = it.value
            //call.putValueArgument(0, it.value)
            IrReturnImpl(
              UNDEFINED_OFFSET,
              UNDEFINED_OFFSET,
              typeTranslator.translateType(targetType),
              it.returnTargetSymbol,
              call
            )
          }
        } else it
      }

      meta(
        enableIr(),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            val derivedConversions = files.flatMap {
              it.findChildrenByClass(KtNamedFunction::class.java)
                .filter { it.isAnnotatedWith(typeProofAnnotation) }
                .toList()
            }.mapNotNull(KtNamedFunction::toTypeConversion)
            typeConversions.addAll(derivedConversions)
            println("Found type conversions: ${typeConversions.joinToString("\n") { "${it.from.text} -> ${it.to.text} : \n ${it.conversion.text}" }}")
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

private fun KtNamedFunction.toTypeConversion(): TypeConversion? {
  println("Considering function:\n $text")
  return receiverTypeReference?.let { from ->
    typeReference?.let { to ->
      TypeConversion(from, to, this, isImplicitConversion())
    }
  }
}
















