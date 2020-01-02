package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.resolve.intersection
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isNothing

data class GivenUpperBound(
  val givenValueParameters: List<ValueParameterDescriptor>,
  val givenUpperBound: KotlinType?
) {
  companion object {

    val givenAnnotationName: FqName = FqName("arrowx.given")

    val Empty: GivenUpperBound =
      GivenUpperBound(
        givenValueParameters = emptyList(),
        givenUpperBound = null
      )

    operator fun invoke(callableMemberDescriptor: CallableMemberDescriptor): GivenUpperBound {
      val givenValueParameters = callableMemberDescriptor.valueParameters
        .mapNotNull {
          if (it.type.annotations.findAnnotation(givenAnnotationName) != null)
            it
          else null
        }
      return if (givenValueParameters.isEmpty()) Empty
      else {
        val intersection = givenValueParameters.fold(callableMemberDescriptor.builtIns.nothingType as KotlinType) { a, b ->
          if (a.isNothing()) b.type
          else a.intersection(b.type)
        }
        GivenUpperBound(givenValueParameters, intersection)
      }
    }

  }
}

