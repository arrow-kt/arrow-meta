package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.typeProofs
import arrow.meta.proofs.Proof
import arrow.meta.proofs.extensions
import com.intellij.openapi.module.ModuleComponent
import com.intellij.openapi.project.Project
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
        descriptor.module.typeProofs.containsImportPath(it)
      }?.let {
        if (it) ImportDescriptorResult.ALREADY_IMPORTED
        else null
      } ?: delegate.importDescriptor(file, descriptor, forceAllUnderImport)
    }

  override fun isImportedWithDefault(importPath: ImportPath, contextFile: KtFile): Boolean =
    Log.Verbose({ "MetaImportInsertHelper.isImportedWithDefault: $importPath, $contextFile, $this" }) {
      contextFile.findModuleDescriptor().typeProofs.containsImportPath(importPath.fqName) ||
        delegate.isImportedWithDefault(importPath, contextFile)
    }

  private fun List<Proof>.containsImportPath(importFqName: FqName): Boolean =
    Log.Verbose({ "MetaImportInsertHelper.containsImportPath: proofs size [${this@containsImportPath.size}] $importFqName, $this" }) {
      val names = importableNames()
      names.any {
        it == importFqName
      }
    }

  private fun List<Proof>.importableNames(): Set<FqName> =
    extensions().flatMap { extension ->
      extension.through.returnType?.let { type ->
        type.memberScope.getContributedDescriptors { true }.map {
          FqName(
            it.fqNameSafe.asString()
              .replace(".${type.constructor.declarationDescriptor?.name}.", ".")
          )
        }
      } ?: emptyList()
    }.toSet()


  override fun isImportedWithLowPriorityDefaultImport(importPath: ImportPath, contextFile: KtFile): Boolean =
    Log.Verbose({ "MetaImportInsertHelper.isImportedWithLowPriorityDefaultImport: $importPath, $contextFile, $this" }) {
      delegate.isImportedWithDefault(importPath, contextFile)
    }

  override fun mayImportOnShortenReferences(descriptor: DeclarationDescriptor): Boolean =
    Log.Verbose({ "MetaImportInsertHelper.mayImportOnShortenReferences: $descriptor, $this" }) {
      delegate.mayImportOnShortenReferences(descriptor)
    }
}

class MetaImportInsertHelper(val project: Project) : ModuleComponent {

  val delegate: ImportInsertHelper = project.getComponent(ImportInsertHelper::class.java)

  override fun initComponent() {
    Log.Verbose({ "MetaImportInsertHelper.initComponent" }) {
      project.replaceComponent<ImportInsertHelper> { Helper(delegate) }
    }
  }

  override fun disposeComponent() {
    Log.Verbose({ "MetaImportInsertHelper.disposeComponent" }) {
      project.replaceComponent<ImportInsertHelper> { delegate }
    }
  }


}

inline fun <reified A> Project.replaceComponent(f: (A) -> A): Unit {
  picoContainer.safeAs<DefaultPicoContainer>()?.apply {
    getComponentAdapterOfType(A::class.java)?.let { componentAdapter ->
      val instance = componentAdapter.getComponentInstance(this) as? A
      if (instance != null) {
        val newInstance = f(instance)
        val key = componentAdapter.componentKey
        unregisterComponent(key)
        registerComponentInstance(key, newInstance as Any)
      }
    }
  }
}
