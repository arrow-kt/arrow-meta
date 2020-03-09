package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.Proof
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.constituentTypes

/**
 * Given a subtype such as a data class or a tuple this function determines
 * the candidates available to fulfill the parts.
 * We here define that a product type is structurally any interface class or object that
 * contains one of the `operator componentN` overrides and used their return type to determine
 * the types of the parts. This function finds all proofs matching the parts necessary to construct a proof
 * for the product.
 */
fun List<Proof>.derivingCandidates(
  compilerContext: CompilerContext,
  subType: KotlinType,
  superType: KotlinType
): List<Proof>  = TODO()

fun KotlinType.productTypes(): List<KotlinType> =
  memberScope.getContributedDescriptors { true }
    .filterIsInstance<SimpleFunctionDescriptor>()
    .filter { it.isOperator && it.name.asString().startsWith("component") }
    .mapNotNull { it.returnType }