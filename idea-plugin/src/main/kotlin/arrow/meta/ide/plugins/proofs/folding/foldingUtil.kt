package arrow.meta.ide.plugins.proofs.folding

import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.types.KotlinType

internal fun KtTypeReference.getType(): KotlinType? = analyze()[BindingContext.TYPE, this]
