package arrow.meta.phases.analysis

import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportInfo

/**
 * @see [ExtensionPhase]
 * @see [arrow.meta.dsl.analysis.AnalysisSyntax.extraImports]
 */
interface ExtraImports : ExtensionPhase {
  fun CompilerContext.extraImports(ktFile: KtFile): Collection<KtImportInfo>
}
