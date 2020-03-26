package arrow.meta.ide.plugins.proofs.annotators

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.resolve.validateConstructorCall
import com.intellij.lang.annotation.Annotator
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.quickfix.AddModifierFix
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun IdeMetaPlugin.refinementAnnotator(): ExtensionPhase =
  addAnnotator(
    annotator = Annotator { element, holder -> // in some situations there are 2 or more error annotations
      element.safeAs<KtCallElement>()?.let { ktCall ->
        val ctx = ktCall.analyze(bodyResolveMode = BodyResolveMode.FULL)
        val calls = ctx.getSliceContents(BindingContext.RESOLVED_CALL)
        calls.forEach { (call, resolvedCall) ->
          resolvedCall?.let {
            if (call.callElement == element) {
              val module = resolvedCall.resultingDescriptor.module
              val compilerContext = CompilerContext(project = element.project, eval = {
                KotlinJsr223StandardScriptEngineFactory4Idea().scriptEngine.eval(it)
              })
              compilerContext.module = module
              val validation = compilerContext.validateConstructorCall(it)
              validation.filterNot { entry -> entry.value }.forEach { (msg, _) ->
                holder.createErrorAnnotation(element, msg)//.registerUniversalFix(AddModifierFix(f, KtTokens.SUSPEND_KEYWORD), f.identifyingElement?.textRange, null)
              }
            }
          }
        }

//            holder.createErrorAnnotation(f, "This is a call element")?.let { error ->
//             // error.registerUniversalFix(AddModifierFix(f, KtTokens.SUSPEND_KEYWORD), f.identifyingElement?.textRange, null)
//            }
      }
    }
  )