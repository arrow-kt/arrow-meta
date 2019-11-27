package arrow.meta.ide.phases.resolve.proofs

import arrow.meta.ide.plugins.proofs.lexicalScope
import arrow.meta.phases.resolve.typeProofs
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.lazy.FileScopeProvider
import org.jetbrains.kotlin.resolve.lazy.FileScopes
import org.jetbrains.kotlin.resolve.lazy.ImportForceResolver
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.scopes.LexicalScope

class MetaFileScopeProvider(
  val session: ResolveSession,
  val delegate: FileScopeProvider
) : FileScopeProvider by delegate {
  override fun getFileResolutionScope(file: KtFile): LexicalScope =
    Log.Verbose({ "MetaFileScopeProvider.getFileResolutionScope: $file, scope: $this" }) {
      val module = session.moduleDescriptor
      val originalFileScope = delegate.getFileResolutionScope(file)
      val proofsScope = module.typeProofs.lexicalScope(originalFileScope, originalFileScope.ownerDescriptor)
      proofsScope
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