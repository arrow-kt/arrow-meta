package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.phases.resolve.typeProofs
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.FileScopeProvider
import org.jetbrains.kotlin.resolve.lazy.FileScopes
import org.jetbrains.kotlin.resolve.lazy.ImportForceResolver
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.ImportingScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.utils.addImportingScope
import org.jetbrains.kotlin.utils.Printer

class MetaFileScopeProvider(
  val module: ModuleDescriptor,
  val delegate: FileScopeProvider
) : FileScopeProvider by delegate {
  override fun getFileResolutionScope(file: KtFile): LexicalScope =
    Log.Verbose({ "MetaFileScopeProvider.getFileResolutionScope: $file, scope: $this" }) {
      val fileScope = delegate.getFileResolutionScope(file)
      val proofs = module.typeProofs
      if (proofs.isEmpty()) fileScope
      else {
        val proofsMemberScope = proofs.chainedMemberScope()
        val proofsModifiedScope = fileScope.addImportingScope(
          MemberScopeToImportingScopeAdapter(null, proofsMemberScope)
        )
        proofsModifiedScope
      }
    }


  override fun getFileScopes(file: KtFile): FileScopes =
    Log.Verbose({ "MetaFileScopeProvider.getFileScopes: $file" }) {
      delegate.getFileScopes(file)
    }

  override fun getImportResolver(file: KtFile): ImportForceResolver =
    Log.Verbose({ "MetaFileScopeProvider.getImportResolver: $file" }) {
      delegate.getImportResolver(file)
    }
}

private class MemberScopeToImportingScopeAdapter(override val parent: ImportingScope?, val memberScope: MemberScope) : ImportingScope {
  override fun getContributedPackage(name: Name): PackageViewDescriptor? = null

  override fun getContributedDescriptors(
    kindFilter: DescriptorKindFilter,
    nameFilter: (Name) -> Boolean,
    changeNamesForAliased: Boolean
  ) = memberScope.getContributedDescriptors(kindFilter, nameFilter)

  override fun getContributedClassifier(name: Name, location: LookupLocation) = memberScope.getContributedClassifier(name, location)

  override fun getContributedVariables(name: Name, location: LookupLocation) = memberScope.getContributedVariables(name, location)

  override fun getContributedFunctions(name: Name, location: LookupLocation) = memberScope.getContributedFunctions(name, location)

  override fun equals(other: Any?) = other is MemberScopeToImportingScopeAdapter && other.memberScope == memberScope

  override fun hashCode() = memberScope.hashCode()

  override fun toString() = "${this::class.java.simpleName} for $memberScope"

  override fun computeImportedNames(): Set<Name>? = null

  override fun printStructure(p: Printer) {
    p.println(this::class.java.simpleName)
    p.pushIndent()

    memberScope.printScopeStructure(p.withholdIndentOnce())

    p.popIndent()
    p.println("}")
  }
}