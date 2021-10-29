package arrow.meta.plugins.proofs.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.getOrCreateBaseDirectory
import arrow.meta.plugins.proofs.phases.contextualAnnotations
import arrow.meta.plugins.proofs.phases.isProof
import arrow.meta.plugins.proofs.phases.resolve.cache.skipPackages
import org.jetbrains.kotlin.analyzer.AnalysisResult
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.containingPackage
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.platform
import org.jetbrains.kotlin.resolve.multiplatform.isCommonSource

private const val genkey: String = "given.generated"

fun Meta.generateGivenPreludeFile(): ExtensionPhase =
  analysis(
    doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
      val alreadyGenerated = get<Boolean>(genkey)
      if (alreadyGenerated != null && alreadyGenerated) null
      else {
        val path = getOrCreateBaseDirectory(configuration)
        generateGivenFiles(module, path.absolutePath)
        set(genkey, true)
        AnalysisResult.RetryWithAdditionalRoots(bindingTrace.bindingContext, module, emptyList(), listOfNotNull(path))
      }
    }
  )

private fun CompilerContext.generateGivenFiles(module: ModuleDescriptor, parentPath: String): List<java.io.File> =
  module.renderFunctions(this).mapNotNull { (fn, source) ->
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

private fun ModuleDescriptor.renderFunctions(ctx: CompilerContext): List<Pair<DeclarationDescriptor, String>> =
  declarationsWithGivenArguments()
    .mapNotNull {
      val generatedSources = ctx.internalGivenFunction(it)
      if (generatedSources != null) {
        it to generatedSources
      } else null
    }

private fun CompilerContext.internalGivenFunction(f: DeclarationDescriptor): String? =
  f.run {
    val s =
      if (skipGeneration(f)) null
      else when (this) {
        is CallableDescriptor -> {
          """ 
            package ${f.containingPackage()}
            @arrow.CompileTime
            internal fun ${typeParameters.render()} ${extensionReceiverParameter.render()} ${dispatchReceiverParameter.render()} $name(${valueParameters.renderParameters()}): $returnType = 
              $name(${valueParameters.renderAsArguments()}) 
            """.trimIndent()
        }
        is ClassDescriptor -> {
          if (unsubstitutedPrimaryConstructor?.valueParameters?.isNotEmpty() == true) {
            """ 
            package ${f.containingPackage()}
            @arrow.CompileTime
            internal fun ${declaredTypeParameters.render()} $name(${unsubstitutedPrimaryConstructor?.valueParameters?.renderParameters()}): $defaultType = 
              $name(${unsubstitutedPrimaryConstructor?.valueParameters?.renderAsArguments()}) 
            """.trimIndent()
          } else ""
        }
        else -> ""
      }
    s
  }

private fun skipGeneration(f: DeclarationDescriptor) =
  f.platform != CommonPlatforms.defaultCommonPlatform &&
    (f.findPsi()?.containingFile as? KtFile)?.isCommonSource == true

private fun List<TypeParameterDescriptor>.render(): String =
  if (isEmpty()) ""
  else joinToString(prefix = "<", postfix = ">") { it.name.asString() }

private fun ReceiverParameterDescriptor?.render(): String {
  return this?.value?.type?.toString()?.let { "$it." } ?: ""
}

private fun List<ValueParameterDescriptor>.renderParameters(): String =
  joinToString {
    val context = it.contextualAnnotations().firstOrNull()
    if (it.isProof() && context != null) "@$context ${it.name}: ${it.type} = TODO(\"Compile time replaced\")"
    else "${it.name}: ${it.type}"
  } + ", unit: Unit = Unit"

private fun List<ValueParameterDescriptor>.renderAsArguments(): String =
  joinToString { it.name.asString() }

private tailrec fun ModuleDescriptor.declarationsWithGivenArguments(
  acc: List<DeclarationDescriptor> = emptyList(),
  packages: List<FqName> = listOf(FqName.ROOT),
  skipPacks: Set<FqName> = skipPackages
): List<DeclarationDescriptor> =
  when {
    packages.isEmpty() -> acc
    else -> {
      val current = packages.first()
      val topLevelDescriptors = getPackage(current).memberScope.getContributedDescriptors { true }.toList()
      val memberDescriptors = topLevelDescriptors.filterIsInstance<ClassDescriptor>().flatMap {
        it.unsubstitutedMemberScope.getContributedDescriptors { true }.toList()
      }
      val allPackageDescriptors = topLevelDescriptors + memberDescriptors
      val packagedProofs = allPackageDescriptors
        .filter {
          (it is ClassDescriptor && it.isProof()) ||
            it is CallableDescriptor && it.valueParameters.any { it.isProof() }
        }
      val remaining = (getSubPackagesOf(current) { true } + packages.drop(1)).filter { it !in skipPacks }
      declarationsWithGivenArguments(acc + packagedProofs.asSequence(), remaining)
    }
  }
