package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.diagnostic.MetaDefaultErrorMessages
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.bindingCtx
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.annotation.Annotator
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val IdeMetaPlugin.proofAnnotator: ExtensionPhase
  get() = addAnnotator(
    annotator = Annotator { element, holder ->
      element.safeAs<KtNamedFunction>()?.let { f ->
        val diagnostics = f.bindingCtx()?.diagnostics
        diagnostics?.filter { it.factory is MetaDefaultErrorMessages }
      }
    }
  )