package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.plugins.proofs.phases.proofs
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.callables
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.testFramework.registerServiceInstance
import com.intellij.util.pico.DefaultPicoContainer
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.findModuleDescriptor
import org.jetbrains.kotlin.idea.imports.importableFqName
import org.jetbrains.kotlin.idea.util.ImportDescriptorResult
import org.jetbrains.kotlin.idea.util.ImportInsertHelper
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.ImportPath
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import java.util.*

private class Helper(private val delegate: ImportInsertHelper) : ImportInsertHelper() {
  override val importSortComparator: Comparator<ImportPath>
    get() = Log.Verbose({ "MetaImportInsertHelper.importSortComparator" }) {
      delegate.importSortComparator
    }

  override fun importDescriptor(file: KtFile, descriptor: DeclarationDescriptor, forceAllUnderImport: Boolean): ImportDescriptorResult =
    Log.Verbose({ "MetaImportInsertHelper.importDescriptor: $file, $descriptor, $forceAllUnderImport, $this" }) {
      descriptor.importableFqName?.let {
        descriptor.module.proofs.containsImportPath(it)
      }?.let {
        if (it) ImportDescriptorResult.ALREADY_IMPORTED
        else null
      } ?: delegate.importDescriptor(file, descriptor, forceAllUnderImport)
    }

  override fun isImportedWithDefault(importPath: ImportPath, contextFile: KtFile): Boolean =
    Log.Verbose({ "MetaImportInsertHelper.isImportedWithDefault: $importPath, $contextFile, $this" }) {
      contextFile.findModuleDescriptor().proofs.containsImportPath(importPath.fqName) ||
        delegate.isImportedWithDefault(importPath, contextFile)
    }

  private fun List<Proof>.importableNames(): Set<FqName> =
    flatMap { it.callables() }
      .map { FqName(it.fqNameSafe.asString().replace(".${it.containingDeclaration.name}.", ".")) }
      .toSet()

  private fun List<Proof>.containsImportPath(importFqName: FqName): Boolean {
    val names = importableNames()
    return Log.Silent({ "MetaImportInsertHelper.containsImportPath: importable names [${names}] : $importFqName, $this" }) {
      names.any {
        it == importFqName
      }
    }
  }

  override fun isImportedWithLowPriorityDefaultImport(importPath: ImportPath, contextFile: KtFile): Boolean =
    Log.Verbose({ "MetaImportInsertHelper.isImportedWithLowPriorityDefaultImport: $importPath, $contextFile, $this" }) {
      contextFile.findModuleDescriptor().proofs.containsImportPath(importPath.fqName) ||
        delegate.isImportedWithDefault(importPath, contextFile)
    }

  override fun mayImportOnShortenReferences(descriptor: DeclarationDescriptor): Boolean {
    val comparingPath = descriptor.importableFqName ?: descriptor.fqNameSafe
    return Log.Verbose({ "MetaImportInsertHelper.mayImportOnShortenReferences: comparingPath $comparingPath $descriptor, $this" }) {
      descriptor.module.proofs.containsImportPath(comparingPath) ||
        delegate.mayImportOnShortenReferences(descriptor)
    }
  }
}

class MetaImportInsertHelper(val project: Project) : ProjectComponent {

  val delegate: ImportInsertHelper = project.getService(ImportInsertHelper::class.java)

  override fun initComponent() {
    Log.Verbose({ "MetaImportInsertHelper.initComponent" }) {
      project.replaceImportsInsertHelper { Helper(delegate) }
    }
  }

  override fun disposeComponent() {
    Log.Verbose({ "MetaImportInsertHelper.disposeComponent" }) {
      project.replaceImportsInsertHelper { delegate }
    }
  }

  private inline fun Project.replaceImportsInsertHelper(f: (ImportInsertHelper) -> ImportInsertHelper): Unit {
    picoContainer.safeAs<DefaultPicoContainer>()?.apply {
      getComponentAdapterOfType(ImportInsertHelper::class.java)?.apply {
        val instance = project.getService(ImportInsertHelper::class.java)
        if (instance != null) {
          val newInstance = f(instance)
          unregisterComponent(componentKey)
          registerServiceInstance(ImportInsertHelper::class.java, newInstance)
        }
      }
    }
  }

}


