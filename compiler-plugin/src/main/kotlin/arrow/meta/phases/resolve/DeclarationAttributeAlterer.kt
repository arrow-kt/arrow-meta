package arrow.meta.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.psi.KtModifierListOwner

/**
 * @see [ExtensionPhase]
 * @see [arrow.meta.dsl.resolve.ResolveSyntax.declarationAttributeAlterer]
 */
interface DeclarationAttributeAlterer : ExtensionPhase {
  fun CompilerContext.refineDeclarationModality(
    modifierListOwner: KtModifierListOwner,
    declaration: DeclarationDescriptor?,
    containingDeclaration: DeclarationDescriptor?,
    currentModality: Modality,
    isImplicitModality: Boolean
  ): Modality?
}