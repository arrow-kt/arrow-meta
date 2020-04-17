package arrow.meta.ide.plugins.proofs.folding

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

val IdeMetaPlugin.codeFolding: ExtensionPhase
  get() = Composite(
    codeFoldingOnUnions,
    codeFoldingOnTuples,
    // codeFoldingOnKinds, // temporary disabled to avoid confusion due to issues
    foldingCaretListener
  )

internal fun KtTypeReference.getType(): KotlinType? = analyze()[BindingContext.TYPE, this]