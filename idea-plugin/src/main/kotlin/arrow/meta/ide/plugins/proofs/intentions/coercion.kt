package arrow.meta.ide.plugins.proofs.intentions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.coerceProof
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.debugger.sequence.psi.resolveType
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode

fun IdeMetaPlugin.coercionIntention(): ExtensionPhase =
  addIntention(
    text = "Make coercion explicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, caretOffset: Int ->
      when (ktCall) {
        is KtProperty -> isPropertyCoerced(ktCall)
        else -> false
      }
    },
    applyTo = { ktCall, editor ->
      // replace with proof previously found
    }
  )

private fun isPropertyCoerced(ktCall: KtProperty): Boolean {
  val (supertype, subtype) = ktCall.type()!! to ktCall.initializer?.resolveType()!!
  val isSubtypeOf = baseLineTypeChecker.isSubtypeOf(subtype, supertype)

  if (isSubtypeOf) return false

  return Log.Verbose({ "Found ${if (this) "" else "no"} proof for $supertype $subtype on ${ktCall.text}" }) {
    val ctx = ktCall.analyze(bodyResolveMode = BodyResolveMode.FULL)
    val calls = ctx.getSliceContents(BindingContext.RESOLVED_CALL)

    // TODO Currently there are no calls for a property (?)
    val isProofSubtype = calls.filter { (call, resolvedCall) -> resolvedCall != null }
      .any { (call, resolvedCall) ->
        val compilerContext = CompilerContext(project = call.callElement.project, eval = {
          KotlinJsr223StandardScriptEngineFactory4Idea().scriptEngine.eval(it)
        }).apply {
          this.module = resolvedCall.resultingDescriptor.module
        }

        compilerContext.coerceProof(subtype, supertype) != null
      }

    !isSubtypeOf && isProofSubtype
  }
}