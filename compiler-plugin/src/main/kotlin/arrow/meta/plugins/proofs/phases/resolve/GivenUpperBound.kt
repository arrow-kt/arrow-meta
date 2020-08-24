package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.codegen.ir.substitutedValueParameters
import arrow.meta.phases.resolve.intersection
import arrow.meta.plugins.proofs.phases.ArrowGivenProof
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isNothing

data class GivenUpperBound(
  val givenValueParameters: List<Pair<ValueParameterDescriptor, KotlinType>>,
  val givenUpperBound: KotlinType?,
) {
  companion object {
    val Empty: GivenUpperBound =
      GivenUpperBound(
        givenValueParameters = emptyList(),
        givenUpperBound = null
      )

    operator fun invoke(f: CallableMemberDescriptor, call: IrCall): GivenUpperBound =
      f.substitutedValueParameters(call)
        .filter { it.first.type.annotations.hasAnnotation(ArrowGivenProof) }
        .takeIf { it.isNotEmpty() }
        ?.run {
          GivenUpperBound(this, fold(f.builtIns.nothingType as KotlinType) { a, (_, b) ->
            if (a.isNothing()) b
            else a.intersection(b)
          })
        } ?: Empty
  }
}

