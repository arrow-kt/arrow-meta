package arrow.meta.ide.phases.resolve.proofs

import com.google.common.collect.ImmutableMap
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.pico.DefaultPicoContainer
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.util.ImportInsertHelper
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import org.jetbrains.kotlin.resolve.diagnostics.Diagnostics
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import org.jetbrains.kotlin.util.slicedMap.WritableSlice
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class MetaBindingContext(val delegate: BindingContext, val trace: BindingTrace) : BindingContext by delegate {
  override fun <K : Any?, V : Any?> getKeys(p0: WritableSlice<K, V>?): Collection<K> =
    Log.Silent({ "MetaBindingTrace.getKeys $p0 $this" }) {
      delegate.getKeys(p0)
    }

  override fun getType(p0: KtExpression): KotlinType? =
    Log.Silent({ "MetaBindingTrace.getType $p0 $this" }) {
      delegate.getType(p0)
    }

  override fun <K : Any?, V : Any?> get(p0: ReadOnlySlice<K, V>?, p1: K): V? =
    Log.Silent({ "MetaBindingTrace.get $p0 $this" }) {
      delegate.get(p0, p1)
    }

  override fun getDiagnostics(): Diagnostics =
    Log.Silent({ "MetaBindingTrace.getDiagnostics $this" }) {
      delegate.diagnostics
    }

  override fun addOwnDataTo(p0: BindingTrace, p1: Boolean) =
    Log.Silent({ "MetaBindingTrace.addOwnDataTo $p0 $p1 $this" }) {
      delegate.addOwnDataTo(p0, p1)
    }

  override fun <K : Any?, V : Any?> getSliceContents(p0: ReadOnlySlice<K, V>): ImmutableMap<K, V> =
    Log.Silent({ "MetaBindingTrace.getSliceContents $p0 $this" }) {
      delegate.getSliceContents(p0)
    }
}

class MetaBindingTrace(val delegate: BindingTrace) : BindingTrace by delegate {

  lateinit var module: ModuleDescriptor

  override fun report(p0: Diagnostic) =
    Log.Silent({ "MetaBindingTrace.report $this" }) {
      delegate.report(p0)
    }

  override fun <K : Any?, V : Any?> getKeys(p0: WritableSlice<K, V>?): MutableCollection<K> =
    Log.Silent({ "MetaBindingTrace.getKeys $p0 $this" }) {
      delegate.getKeys(p0)
    }

  override fun getBindingContext(): BindingContext =
    Log.Silent({ "MetaBindingTrace.getBindingContext $this" }) {
      MetaBindingContext(delegate.bindingContext, this)
    }

  override fun <K : Any?, V : Any?> record(p0: WritableSlice<K, V>?, p1: K, p2: V) =
    Log.Silent({ "MetaBindingTrace.record $this" }) { //rendering arguments will cause early descriptor init
      delegate.record(p0, p1, p2)
    }

  override fun <K : Any?> record(p0: WritableSlice<K, Boolean>?, p1: K) =
    Log.Silent({ "MetaBindingTrace.record $this" }) { //rendering arguments will cause early descriptor init
      delegate.record(p0, p1)
    }

  override fun getType(p0: KtExpression): KotlinType? =
    Log.Silent({ "MetaBindingTrace.getType $p0 $this" }) {
      delegate.getType(p0)
    }

  override fun wantsDiagnostics(): Boolean =
    Log.Silent({ "MetaBindingTrace.wantsDiagnostics $this" }) {
      delegate.wantsDiagnostics()
    }

  override fun <K : Any?, V : Any?> get(p0: ReadOnlySlice<K, V>?, p1: K): V? =
    Log.Silent({ "MetaBindingTrace.get $p0 $p1" }) {
      delegate.get(p0, p1)
    }

  override fun recordType(p0: KtExpression, p1: KotlinType?) =
    Log.Silent({ "MetaBindingTrace.recordType $p0 $p1" }) {
      delegate.recordType(p0, p1)
    }
}

class MetaCodeAnalyzerInitializerHelper(val delegate: CodeAnalyzerInitializer) : CodeAnalyzerInitializer {
  override fun createTrace(): BindingTrace =
    Log.Silent({ "MetaCodeAnalyzerInitializerHelper.createTrace $this" }) {
      MetaBindingTrace(delegate.createTrace())
    }

  override fun initialize(trace: BindingTrace, module: ModuleDescriptor, codeAnalyzer: KotlinCodeAnalyzer) =
    Log.Silent({ "MetaCodeAnalyzerInitializerHelper.initialize $trace, $module, $codeAnalyzer" }) {
      trace.safeAs<MetaBindingTrace>()?.apply {
        this.module = module
      }
      delegate.initialize(trace, module, codeAnalyzer)
    }
}

class MetaCodeAnalyzerInitializer(val project: Project) : ProjectComponent {

  val delegate: CodeAnalyzerInitializer = project.getService(CodeAnalyzerInitializer::class.java)

  override fun initComponent() {
    Log.Verbose({ "MetaCodeAnalyzerInitializer.initComponent" }) {
      project.replaceCodeAnalyzerInitializer { MetaCodeAnalyzerInitializerHelper(delegate) }
    }
  }

  override fun disposeComponent() {
    Log.Verbose({ "MetaCodeAnalyzerInitializer.disposeComponent" }) {
      project.replaceCodeAnalyzerInitializer { delegate }
    }
  }

  private inline fun Project.replaceCodeAnalyzerInitializer(f: (CodeAnalyzerInitializer) -> CodeAnalyzerInitializer): Unit {
    picoContainer.safeAs<DefaultPicoContainer>()?.apply {
      getComponentAdapterOfType(CodeAnalyzerInitializer::class.java)?.apply {
        val instance = getComponentInstance(componentKey) as? CodeAnalyzerInitializer
        if (instance != null) {
          val newInstance = f(instance)
          unregisterComponent(componentKey)
          registerServiceInstance(CodeAnalyzerInitializer::class.java, newInstance)
        }
      }
    }
  }

}