package arrow.meta.ide.dsl.editor.quickFix

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.intention.IntentionSyntax
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.intention.IntentionAction
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixActionBase
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.idea.quickfix.QuickFixes

interface QuickFixSyntax {

  /**
   * registers a [QuickFixContributor] with the specified [intentions] and [ktIntentions].
   * ```kotlin:ank
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
   * import org.jetbrains.kotlin.diagnostics.DiagnosticFactory1
   * import org.jetbrains.kotlin.diagnostics.PositioningStrategies
   * import org.jetbrains.kotlin.diagnostics.Severity
   * import org.jetbrains.kotlin.idea.quickfix.AddModifierFix
   * import org.jetbrains.kotlin.lexer.KtTokens
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   *
   * //sampleStart
   * val IdeMetaPlugin.quickFixes: Plugin
   *   get() = "QuickFix Contributor" {
   *     meta(
   *       addQuickFixContributor(
   *         ktIntentions = listOf(
   *           ktQuickFixIntention(
   *             DiagnosticFactory1.create<KtNamedFunction, SimpleFunctionDescriptor>(Severity.ERROR, PositioningStrategies.DECLARATION_SIGNATURE),
   *             AddModifierFix.createFactory(KtTokens.SUSPEND_KEYWORD)
   *           )
   *         )
   *       )
   *     )
   *   }
   * //sampleEnd
   * ```
   */
  fun IdeMetaPlugin.addQuickFixContributor(
    intentions: List<QuickFixIntention> = emptyList(),
    ktIntentions: List<KtQuickFixIntention> = emptyList()
  ): ExtensionPhase =
    extensionProvider(
      QuickFixContributor.EP_NAME,
      quickFixContributor(intentions, ktIntentions)
    )

  /**
   * @param ktIntentions use [ktQuickFixIntention]
   * @param intentions use [quickFixIntention]
   */
  fun QuickFixSyntax.quickFixContributor(
    intentions: List<QuickFixIntention> = emptyList(),
    ktIntentions: List<KtQuickFixIntention> = emptyList()
  ): QuickFixContributor =
    object : QuickFixContributor {
      override fun registerQuickFixes(quickFixes: QuickFixes): Unit {
        intentions.forEach { (error, fix) ->
          quickFixes.register(error, *fix.toTypedArray())
        }
        ktIntentions.forEach { (error, fix) ->
          quickFixes.register(error, *fix.toTypedArray())
        }
      }
    }

  /**
   * The default values are derived from [KotlinIntentionActionsFactory].
   * @see KotlinSingleIntentionActionFactory and all its Subtypes for examples
   * @see QuickFixActionBase and all its Subtypes for [action] or [actionsForAll]
   * @param actionsForAll provide for all errors a list of generalized Fixes
   */
  fun QuickFixSyntax.ktIntention(
    action: (diagnostic: Diagnostic) -> IntentionAction? = Noop.nullable1(),
    isApplicableForCodeFragment: Boolean = false,
    actionsForAll: (diagnostics: List<Diagnostic>) -> List<IntentionAction> = Noop.emptyList1()
  ): KotlinSingleIntentionActionFactory =
    object : KotlinSingleIntentionActionFactory() {
      override fun createAction(diagnostic: Diagnostic): IntentionAction? =
        action(diagnostic)

      override fun doCreateActionsForAllProblems(sameTypeDiagnostics: Collection<Diagnostic>): List<IntentionAction> =
        actionsForAll(sameTypeDiagnostics.toList())

      override fun isApplicableForCodeFragment(): Boolean =
        isApplicableForCodeFragment
    }

  /**
   * @see ktIntention
   * @see IntentionSyntax
   */
  fun QuickFixSyntax.quickFixIntention(
    respondOn: DiagnosticFactory<out Diagnostic>,
    vararg intentions: IntentionAction = emptyArray()
  ): QuickFixIntention =
    QuickFixIntention(respondOn, intentions.toList())

  /**
   * @see ktIntention
   */
  fun QuickFixSyntax.ktQuickFixIntention(
    respondOn: DiagnosticFactory<out Diagnostic>,
    vararg intentions: KotlinSingleIntentionActionFactory = emptyArray()
  ): KtQuickFixIntention =
    KtQuickFixIntention(respondOn, intentions.toList())
}

data class QuickFixIntention(
  val respondOn: DiagnosticFactory<out Diagnostic>,
  val intentions: List<IntentionAction>
)

data class KtQuickFixIntention(
  val respondOn: DiagnosticFactory<out Diagnostic>,
  val intentions: List<KotlinSingleIntentionActionFactory>
)
