package arrow.meta.ide.plugins.proofs.annotators.refinement

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.sequenceCalls
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.resolve.validateConstructorCall
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.jsr223.KotlinJsr223StandardScriptEngineFactory4Idea
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.concurrent.Callable
import javax.script.ScriptEngine

internal val IdeMetaPlugin.refinementCallSite: Annotator
  get() = Annotator { element, holder ->
    element.safeAs<KtCallElement>()?.sequenceCalls { call, resolvedCall ->
      val module = resolvedCall.resultingDescriptor.module
      val compilerContext = CompilerContext(project = element.project, eval = { source ->
        emptyMap<String, Boolean>()
        try {
          scriptEngine?.eval(source) ?: emptyMap<String, Boolean>()
        } catch (t: Error) { //this happens the first time this is called
          Log.Verbose({ "Detected $t initializing KotlinJsr223StandardScriptEngineFactory4Idea: for `$resolvedCall`" }) {
            emptyMap<String, Boolean>()
          }
        }
      })
      compilerContext.module = module
      val validation = compilerContext.validateConstructorCall(resolvedCall)
      validation.filterNot { entry -> entry.value }.forEach { (msg, _) ->
        holder.newAnnotation(HighlightSeverity.ERROR, msg)
          .range(element.textRange)
          .tooltip(msg)
      }
    }
  }


/**
 * Please, set the following property in `build.gradle` as true, to view/enable the errors thrown by the engine in the ide.
 * ```
 * runIde {
 *    jvmArgs '-Xmx8G'
 *    systemProperties['idea.is.internal'] = "false" <--
 * }
 * ```
 */
var scriptEngine: ScriptEngine? =
  ApplicationManager.getApplication().executeOnPooledThread(Callable {
    // suppress an unhandled ThreadDeath exception by defining a system property, which is used by the Kotlin plugin's ErrorReporter extension (KotlinErrorReporter).
    val prevValue = System.getProperty("kotlin.fatal.error.notification")
    try {
      System.setProperty("kotlin.fatal.error.notification", "disabled")

      val engine = KotlinJsr223StandardScriptEngineFactory4Idea().scriptEngine
      if (!ApplicationManager.getApplication().isUnitTestMode) {
        // trigger initialization, unless we're in a unit test
        // in unit tests, the engine assumes it's running in unit tests of the Kotlin plugin
        // it throws an AssertionException if the script compiler jars are not in a dir named "dist"
        // this is never the case with Arrow Meta and would always fail
        // https://github.com/JetBrains/kotlin/blob/master/idea/idea-repl/src/org/jetbrains/kotlin/jsr223/KotlinJsr223JvmScriptEngine4Idea.kt#L49
        engine.eval("0")
      }
      engine
    } finally {
      if (prevValue != null) {
        System.setProperty("kotlin.fatal.error.notification", prevValue)
      } else {
        System.clearProperty("kotlin.fatal.error.notification")
      }
    }
  }).get()
