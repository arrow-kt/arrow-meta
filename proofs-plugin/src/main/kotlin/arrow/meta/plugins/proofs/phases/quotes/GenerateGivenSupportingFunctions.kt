package arrow.meta.plugins.proofs.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.isProof
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.containingPackage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe

private const val genkey: String = "given.generated"

fun Meta.generateGivenPreludeFile(): ExtensionPhase =
  analysis(
    doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
      val alreadyGenerated = get<Boolean>(genkey)
      if (alreadyGenerated != null && alreadyGenerated) null
      else {
        val path = files.firstParentPath()?.let { java.io.File(it) }
        path?.let { generateGivenFiles(module, it.absolutePath) }
        set(genkey, true)
        AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), listOfNotNull(path))
      }
    }
  )

private fun CompilerContext.generateGivenFiles(module: ModuleDescriptor, parentPath: String): List<java.io.File> =
  module.renderFunctions(this).map { (fn, source) ->
    java.io.File(
      parentPath,
      "/${fn.fqNameSafe}.given.kt",
    ).also {
      it.createNewFile()
      it.writeText(source)
    }
  }

private fun Iterable<KtFile>.firstParentPath(): String? =
  firstOrNull()?.virtualFilePath?.let { java.io.File(it).parentFile.absolutePath }


private fun ModuleDescriptor.renderFunctions(ctx: CompilerContext): List<Pair<SimpleFunctionDescriptor, String>> =
  functionsWithGivenArguments().filterIsInstance<SimpleFunctionDescriptor>()
    .map {
      it to ctx.internalGivenFunction(it)
    }


private fun CompilerContext.internalGivenFunction(f: SimpleFunctionDescriptor): String =
  f.run {
    val s =
      """ 
        package ${f.containingPackage()}
        @arrow.CompileTime
        internal fun ${typeParameters.render()} ${extensionReceiverParameter.render()} $name(${valueParameters.renderParameters()}): $returnType = $fqNameSafe(${valueParameters.renderAsArguments()}) 
        """.trimIndent()
    s
  }

private fun List<TypeParameterDescriptor>.render(): String =
  if (isEmpty()) ""
  else joinToString(prefix = "<", postfix = ">") { it.name.asString() }

private fun ReceiverParameterDescriptor?.render(): String {
  val receiver = this?.extensionReceiverParameter
  return receiver?.value?.type?.toString() ?: ""
}

private fun List<ValueParameterDescriptor>.renderParameters(): String =
  joinToString {
    if (it.isProof()) "${it.name}: ${it.type} = TODO(\"Compile time replaced\")"
    else "${it.name}: ${it.type}"
  } + ", unit: Unit = Unit"

private fun List<ValueParameterDescriptor>.renderAsArguments(): String =
  joinToString { it.name.asString() }

private tailrec fun ModuleDescriptor.functionsWithGivenArguments(
  acc: List<CallableMemberDescriptor> = emptyList(),
  packages: List<FqName> = listOf(FqName.ROOT)
): List<CallableMemberDescriptor> =
  when {
    packages.isEmpty() -> acc
    else -> {
      val current = packages.first()
      val packagedProofs = getPackage(current).memberScope.getContributedDescriptors { true }
        .filterIsInstance<CallableMemberDescriptor>()
        .filter {
          it.valueParameters.any { it.isProof() }
        }
      val remaining = getSubPackagesOf(current) { true } + packages.drop(1)
      functionsWithGivenArguments(acc + packagedProofs.asSequence(), remaining)
    }
  }
