package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.AnalysisHandler
import arrow.meta.phases.resolve.baseLineTypeChecker
import arrow.meta.plugins.proofs.phases.subtypingProof
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.resolve.cache.initializeProofCache
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.isError

private const val logTypeSize: Int = 50

class ProofTypeChecker(private val compilerContext: CompilerContext) : KotlinTypeChecker {

  override fun isSubtypeOf(p0: KotlinType, p1: KotlinType): Boolean =
    Log.Verbose({ "typeConversion: ${p0.toString().take(logTypeSize)} : ${p1.toString().take(logTypeSize)} -> $this" }) {
      if (!p0.isError && !p1.isError) {
        val result = baseLineTypeChecker.isSubtypeOf(p0, p1)
        if (!result && !p0.isError && !p1.isError) {
          compilerContext.module?.proofs?.subtypingProof(compilerContext, p0, p1) != null
        } else result
      } else false
    }

  override fun equalTypes(p0: KotlinType, p1: KotlinType): Boolean =
    baseLineTypeChecker.equalTypes(p0, p1)

}

fun Meta.registerArgumentTypeResolver(): AnalysisHandler =
  analysis(
    doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
      module.initializeProofCache()
      val argumentTypeResolver: ArgumentTypeResolver = componentProvider.get()
      val typeCheckerField = ArgumentTypeResolver::class.java.getDeclaredField("kotlinTypeChecker").also { it.isAccessible = true }
      typeCheckerField.set(argumentTypeResolver, ProofTypeChecker(this))
      null
    }
  )