package arrow.meta.ide.phases.resolve.proofs

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BindingTraceContext
import org.jetbrains.kotlin.resolve.CodeAnalyzerInitializer
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer

class MetaCodeAnalyzerInitializer : CodeAnalyzerInitializer {
  override fun createTrace(): BindingTrace =
    BindingTraceContext(true)

  override fun initialize(trace: BindingTrace, module: ModuleDescriptor, codeAnalyzer: KotlinCodeAnalyzer) {
    Log.Verbose({"MetaCodeAnalyzerInitializer.initialize: $trace $module, $codeAnalyzer"}) {

    }
  }
}