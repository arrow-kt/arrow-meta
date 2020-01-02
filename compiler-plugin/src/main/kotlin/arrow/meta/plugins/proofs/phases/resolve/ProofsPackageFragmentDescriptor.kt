package arrow.meta.plugins.proofs.phases.resolve

import arrow.meta.Meta
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.resolve.PackageProvider
import arrow.meta.plugins.proofs.phases.Proof
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.MemberScope

class ProofsPackageFragmentDescriptor(
  val module: ModuleDescriptor,
  fqName: FqName,
  val proofs: () -> List<Proof>
) : PackageFragmentDescriptorImpl(module, fqName) {
  override fun getMemberScope(): MemberScope =
    proofs.memberScope()
}

fun Meta.proofsPackageFragmentProvider(): PackageProvider {
  return packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
    object : PackageFragmentProvider {

      override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
        Log.Verbose({ "packageFragmentProvider.getPackageFragments $fqName" }) {
          listOf(ProofsPackageFragmentDescriptor(module, fqName) { module.proofs })
        }


      override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
        Log.Verbose({ "packageFragmentProvider.getSubPackagesOf $fqName" }) {
          emptyList()
        }
    }
  }
}