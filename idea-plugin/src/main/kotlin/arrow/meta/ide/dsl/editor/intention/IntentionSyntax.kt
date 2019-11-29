package arrow.meta.ide.dsl.editor.intention

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.intention.IntentionExtensionProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.impl.config.IntentionActionMetaData
import com.intellij.openapi.editor.Editor
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixContributor
import org.jetbrains.kotlin.psi.KtElement

/**
 * The IDE analysis user code and provides [IntentionAction]'s to either signal error's to user's or resolve them if triggered.
 */
interface IntentionSyntax : IntentionUtilitySyntax {

  /**
   * registers Intention with Metadata such as Html description of the specific behavior of this [intention]
   * @param category are used to order Intentions
   * TODO: This Bails if there is no html and intentionDescription. Try to add them in the Function and not with the resource Folder. But this is fine for now
   */
  fun IdeMetaPlugin.addIntentionWithMetaData(
    category: String,
    intention: IntentionAction
  ): ExtensionPhase =
    IntentionExtensionProvider.RegisterIntentionWithMetaData(intention, category)

  /**
   * registers [intention]
   */
  fun IdeMetaPlugin.addIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    IntentionExtensionProvider.RegisterIntention(intention)

  /**
   * unregisters [intention] from the editor
   */
  fun IdeMetaPlugin.unregisterIntention(
    intention: IntentionAction
  ): ExtensionPhase =
    IntentionExtensionProvider.UnregisterIntention(intention)

  /**
   * registers an Intention and allows to manipulate the editor environment or [K]
   *
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   *
   *
   * val IdeMetaPlugin.example: Plugin
   *  get() = "Intention"{
   *   meta(
   *    addIntention(
   *     text = "Rename HelloWorld Function",
   *     isApplicableTo = { f: KtNamedFunction, caretOffset: Int ->
   *       f.name == "helloWorld"
   *     },
   *     kClass = KtNamedFunction::class.java,
   *     priority = PriorityAction.Priority.NORMAL,
   *     applyTo = { f, editor ->
   *       f.setName("renamed")
   *     }
   *    )
   *  )
   * }
   * ```
   * Here we register an Intention, which renames a `helloWorld` function to `renamed`, whenever the user decides to trigger that Intention
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IdeMetaPlugin.addIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean = Noop.boolean2False,
    applyTo: (element: K, editor: Editor?) -> Unit = Noop.effect2,
    priority: PriorityAction.Priority = PriorityAction.Priority.LOW
  ): ExtensionPhase =
    addIntention(ktIntention(text, kClass, isApplicableTo, applyTo, priority))

  /**
   * Intentions can be enabled and disabled before at application start.
   * @param enabled true set's [intention] available false otherwise
   */
  fun IdeMetaPlugin.setIntentionAsEnabled(intention: IntentionAction, enabled: Boolean): ExtensionPhase =
    IntentionExtensionProvider.SetAvailability(intention, enabled)

  /**
   * This function is similar to [setIntentionAsEnabled] for [IntentionActionMetaData]
   */
  fun IdeMetaPlugin.setIntentionAsEnabled(intention: IntentionActionMetaData, enabled: Boolean): ExtensionPhase =
    IntentionExtensionProvider.SetAvailabilityOnActionMetaData(intention, enabled)

  /**
   * [ktIntention] constructs [SelfTargetingIntention] to resolve universal error's of [K] and [ktIntention] can be composed with [addQuickFixContributor].
   * [text] describes the identifier what the user see's.
   * @param isApplicableTo defines when this intention is available.
   * @param priority position's this intention. [PriorityAction.Priority.TOP] being the highest.
   * @param text needs to be the same as `familyName` in order to create MetaData for an Intention.
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IntentionSyntax.ktIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean = Noop.boolean2False,
    applyTo: (element: K, editor: Editor?) -> Unit = Noop.effect2,
    priority: PriorityAction.Priority = PriorityAction.Priority.LOW
  ): SelfTargetingIntention<K> =
    object : SelfTargetingIntention<K>(kClass, text), PriorityAction {
      override fun applyTo(element: K, editor: Editor?): Unit =
        applyTo(element, editor)

      override fun isApplicableTo(element: K, caretOffset: Int): Boolean =
        isApplicableTo(element, caretOffset)

      override fun getPriority(): PriorityAction.Priority =
        priority
    }

  /**
   * Default values are derived from [KotlinIntentionActionsFactory].
   * The function [kotlinIntention] is mainly used for [QuickFixContributor].
   */
  fun IntentionSyntax.kotlinIntention(
    createAction: (diagnostic: Diagnostic) -> IntentionAction? = Noop.nullable1(),
    isApplicableForCodeFragment: Boolean = false,
    doCreateActionsForAllProblems: (sameTypeDiagnostics: Collection<Diagnostic>) -> List<IntentionAction> = Noop.emptyList1()
  ): KotlinSingleIntentionActionFactory =
    object : KotlinSingleIntentionActionFactory() {
      override fun createAction(diagnostic: Diagnostic): IntentionAction? =
        createAction(diagnostic)

      override fun doCreateActionsForAllProblems(sameTypeDiagnostics: Collection<Diagnostic>): List<IntentionAction> =
        doCreateActionsForAllProblems(sameTypeDiagnostics)

      override fun isApplicableForCodeFragment(): Boolean =
        isApplicableForCodeFragment
    }
}


