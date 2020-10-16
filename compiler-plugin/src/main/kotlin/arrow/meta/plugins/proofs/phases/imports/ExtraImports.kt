package arrow.meta.plugins.proofs.phases.imports

import arrow.meta.Meta
import arrow.meta.phases.ExtensionPhase
import arrow.meta.plugins.proofs.phases.ArrowProofSet
import arrow.meta.plugins.proofs.phases.ArrowRefined
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val Meta.preludeImports: ExtensionPhase
  get() = extraImports { _ ->
    ArrowProofSet.map { importInfo(it) } +
      (1..22).map { importInfo(FqName("arrow.with.n$it"), alias = Name.identifier("with$it")) } +
      importInfo(FqName("arrow.given")) + importInfo(ArrowRefined)
  }