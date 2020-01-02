package arrow.meta.phases

import arrow.meta.phases.analysis.ElementScope
import arrow.meta.plugins.proofs.phases.Proof
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.container.ComponentProvider
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtPsiFactory
import arrow.meta.plugins.proofs.phases.proofs as tp

/**
 * The Compiler Context represents the environment received by all plugins.
 * The Compiler Context will get more services as they become relevant overtime to the development of compiler plugins.
 */
class CompilerContext(
  val project: Project,
  val messageCollector: MessageCollector?,
  val scope: ElementScope,
  val ktPsiElementFactory: KtPsiFactory = KtPsiFactory(project, false)
) : ElementScope by scope {
  private var md: ModuleDescriptor? = null
  private var cp: ComponentProvider? = null

  val ModuleDescriptor?.proofs: List<Proof>
    get() = this?.tp ?: emptyList()

  var module: ModuleDescriptor?
    get() = md
    set(value) {
      md = value
    }

  var componentProvider: ComponentProvider?
    get() = cp
    set(value) {
      cp = value
    }

  val ctx: CompilerContext = this
}
