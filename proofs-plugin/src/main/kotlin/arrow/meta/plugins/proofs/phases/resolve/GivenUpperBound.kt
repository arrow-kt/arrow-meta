package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.codegen.ir.substitutedValueParameters
import arrow.meta.phases.resolve.intersection
import arrow.meta.plugins.proofs.phases.ArrowGivenProof
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isNothing

data class GivenUpperBound(
  val givenValueParameters: List<Pair<IrValueParameter, IrType?>>,
  val givenUpperBound: KotlinType?,
) {
  companion object {
    val Empty: GivenUpperBound =
      GivenUpperBound(
        givenValueParameters = emptyList(),
        givenUpperBound = null
      )

    operator fun invoke(call: IrCall): GivenUpperBound =
      call.substitutedValueParameters
        .filter { it.first.type.annotations.hasAnnotation(ArrowGivenProof) }
        .takeIf { it.isNotEmpty() }
        ?.run {
          GivenUpperBound(this, fold(call.symbol.owner.toIrBasedDescriptor().builtIns.nothingType as KotlinType) { a, (_, b) ->
            if (a.isNothing()) b?.originalKotlinType!!
            else a.intersection(b?.originalKotlinType!!)
          })
        } ?: Empty
  }
}

