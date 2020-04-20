package arrow.meta.ide.dsl.editor.intention

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.utils.ktPsiFactory
import arrow.meta.ide.phases.editor.intention.IntentionExtensionProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.impl.config.IntentionActionMetaData
import com.intellij.lang.annotation.Annotator
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElementFactory
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.idea.quickfix.KotlinIntentionActionsFactory
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory
import org.jetbrains.kotlin.idea.quickfix.QuickFixActionBase
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * The IDE analysis user code and provides [IntentionAction]'s to either signal error's to user's or resolve them if triggered.
 */
interface IntentionSyntax : IntentionUtilitySyntax {

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
   * This extension registers an [SelfTargetingIntention].
   * The following example renames a `helloWorld` function to `renamed`, whenever the user decides to trigger that Intention.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.invoke
   * // import com.intellij.codeInsight.intention.PriorityAction
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import com.intellij.openapi.editor.Editor
   *
   * //sampleStart
   * val IdeMetaPlugin.example: IdePlugin
   *  get() = "SampleIntention"{
   *   meta(
   *    addIntention(
   *     text = "Rename HelloWorld Function",
   *     isApplicableTo = { f: KtNamedFunction, caretOffset: Int ->
   *       f.name == "helloWorld"
   *     },
   *     kClass = KtNamedFunction::class.java,
   *     applyTo = { f: KtNamedFunction, editor: Editor ->
   *       f.setName("renamed")
   *     }
   *    )
   *   )
   *  }
   * //sampleEnd
   * ```
   * [IntentionAction] are generally on-demand computations, similar to [AnAction], unless they're composed with other workflows, which trigger them, automatically.
   * @see ktIntention has more information about the parameters.
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IdeMetaPlugin.addIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean = Noop.boolean2False,
    applyTo: KtPsiFactory.(element: K, editor: Editor) -> Unit = Noop.effect3,
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
   * [ktIntention] constructs [SelfTargetingIntention]. SelfTargetingIntentions can be used with [Annotator].
   * @param applyTo allows to resolve the errors on this `element` with [KtPsiFactory], display refined errors through the `editor` and has many other use-cases. For instance Java utilizes [PsiElementFactory]
   * @param text is the displayed text in the ide. In addition, [text] needs to be the same as `familyName` in order to create MetaData for an Intention.
   * @param isApplicableTo defines when this intention is available.
   * @param priority defines the position of this Intention - [PriorityAction.Priority.TOP] being the highest.
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IntentionSyntax.ktIntention(
    text: String = "",
    kClass: Class<K> = KtElement::class.java as Class<K>,
    isApplicableTo: (element: K, caretOffset: Int) -> Boolean = Noop.boolean2False,
    applyTo: KtPsiFactory.(element: K, editor: Editor) -> Unit = Noop.effect3,
    priority: PriorityAction.Priority = PriorityAction.Priority.LOW
  ): SelfTargetingIntention<K> =
    object : SelfTargetingIntention<K>(kClass, text), PriorityAction {
      override fun applyTo(element: K, editor: Editor?): Unit =
        editor?.let { it.project?.ktPsiFactory?.let { factory -> applyTo(factory, element, it) } } ?: Unit

      override fun isApplicableTo(element: K, caretOffset: Int): Boolean =
        isApplicableTo(element, caretOffset)

      override fun getPriority(): PriorityAction.Priority =
        priority
    }

  /**
   * The default values are derived from [KotlinIntentionActionsFactory].
   * @see KotlinSingleIntentionActionFactory and all its Subtypes for examples
   * @see QuickFixActionBase and all its Subtypes for [action] or [actionsForAll]
   * @param actionsForAll provide for all errors a list of generalized Fixes
   */
  fun IntentionSyntax.ktIntention(
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
}
