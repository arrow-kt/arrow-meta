package arrow.meta.ide.dsl.editor.inspection

import arrow.meta.ide.MetaIde
import arrow.meta.ide.dsl.utils.ktPsiFactory
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInspection.GlobalInspectionTool
import com.intellij.codeInspection.InspectionEP
import com.intellij.codeInspection.InspectionProfileEntry
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.LanguageInspectionSuppressors
import com.intellij.codeInspection.LocalInspectionEP
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.inspections.AbstractApplicabilityBasedInspection
import org.jetbrains.kotlin.idea.quickfix.KotlinSuppressIntentionAction
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Loosely speaking, `Inspection's` are easily recognized as "QuickFixes" when the user hit's a KeyShortCut for missing imports.
 * Interestingly enough, despite calling `Inspection's` proverbially "QuickFixes", IntelliJ defines a `QuickFix` as an aggregation of multiple `Intention's`.
 * Whereas `Intention's` analysis your code and users can decide whether they want to apply a suggested Fix,
 * Inspection's improve upon that very idea and are capable to block the user to compile code at the first place.
 * Additionally, we can scope the Fix in `applyTo` locally, for each instance per file, or globally to the whole project, assuming it has a universal refactoring task.
 * There are cases, where an universal Fix, might not be obvious, but that doesn't stop plugin developer's to notify and direct user's to helpful resources about this problem.
 * @see [addApplicableInspection]
 */
interface InspectionSyntax : InspectionUtilitySyntax {
  // TODO: Add more General `Inspection's` can be build with [org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection] e.g.: [org.jetbrains.kotlin.idea.inspections.RedundantSuspendModifierInspection]
  // TODO: for more inspiration LocalQuickFixOnPsiElement

  /**
   * registers a Local ApplicableInspection and has [KtPsiFactory] in Scope to modify the element, project or editor at once within [applyTo].
   * The following example is a simplified purityPlugin, where every function that returns Unit has to be suspended. Otherwise the code can not be compiled.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.MetaIde
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.dsl.utils.intersectFunction
   * import arrow.meta.ide.invoke
   * import arrow.meta.phases.analysis.returnTypeEq
   * import com.intellij.codeHighlighting.HighlightDisplayLevel
   * import com.intellij.codeInspection.ProblemHighlightType
   * import org.jetbrains.kotlin.codegen.coroutines.isSuspendLambdaOrLocalFunction
   * import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
   * import org.jetbrains.kotlin.lexer.KtTokens
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   *
   * //sampleStart
   * val MetaIde.simplyPure: IdePlugin
   *   get() = "Draft PurityPlugin" {
   *     meta(
   *       addApplicableInspection(
   *         defaultFixText = "Simplified PurityPlugin",
   *         inspectionHighlightType = { ProblemHighlightType.ERROR },
   *         kClass = KtNamedFunction::class.java,
   *         inspectionText = { f -> "Teach your users why Function ${f.name} has to be suspended" },
   *         applyTo = { f, project, editor ->
   *           f.addModifier(KtTokens.SUSPEND_KEYWORD)
   *         },
   *         isApplicable = { f: KtNamedFunction ->
   *           !f.hasModifier(KtTokens.SUSPEND_KEYWORD) &&
   *             f.resolveToDescriptorIfAny()?.run {
   *               !isSuspend && !isSuspendLambdaOrLocalFunction() &&
   *                 intersectFunction(returnTypeEq, f) {
   *                   listOf(unitType)
   *                 }.isNotEmpty()
   *               // `intersectFunction` evaluates the return type of the FunctionDescriptor of [f] and all return types of call-sites in the function body,
   *               // returning a list of `KotlinTypes` that intersect with the specified list here `listOf(unitType)`.
   *               // `returnTypeEq` defines type equality where function types are reduced to their return type.
   *             } == true
   *         },
   *         level = HighlightDisplayLevel.ERROR,
   *         groupPath = arrayOf("Meta", "SimplePlugin")
   *       )
   *     )
   *   }
   * //sampleEnd
   * ```
   * Needless to say, the latter implementation is not sufficient enough as a purityPlugin, as the function body of the underlying Call's may have impure Call's.
   * This Plugin will be discoverable in the user setting's under the Path Meta and its groupDisplayName is `SimplePlugin`.
   * In addition, depending on [inspectionHighlightType] and [level] the `lightBulb` changes it's color.
   * @param groupPath
   * @see addLocalInspection
   * @sample arrow.meta.ide.plugins.purity.purity
   */
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> MetaIde.addApplicableInspection(
    defaultFixText: String,
    kClass: Class<K> = KtElement::class.java as Class<K>,
    highlightingRange: (element: K) -> TextRange? = Noop.nullable1(),
    inspectionText: (element: K) -> String,
    applyTo: KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit,
    isApplicable: (element: K) -> Boolean,
    groupPath: Array<String>,
    inspectionHighlightType: (element: K) -> ProblemHighlightType =
      { _ -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING },
    level: HighlightDisplayLevel = HighlightDisplayLevel.WEAK_WARNING,
    enabledByDefault: Boolean = true
  ): ExtensionPhase =
    addLocalInspection(
      applicableInspection(defaultFixText, kClass, highlightingRange, inspectionText, applyTo, isApplicable, inspectionHighlightType, enabledByDefault),
      groupPath,
      level
    )

  fun <K : KtElement> MetaIde.addLocalInspection(
    inspection: AbstractApplicabilityBasedInspection<K>,
    groupPath: Array<String>,
    level: HighlightDisplayLevel = HighlightDisplayLevel.WEAK_WARNING
  ): ExtensionPhase =
    addLocalInspection(inspection, level, inspection.defaultFixText, inspection.defaultFixText, groupPath, inspection.defaultFixText)

  /**
   * registers a GlobalInspection.
   * [InspectionEP] is once again a wrapper over the actual [GlobalInspectionTool].
   * @see addLocalInspection
   * TODO: Add easy first issue for contributor's
   */
  fun MetaIde.addGlobalInspection(
    inspectionTool: GlobalInspectionTool,
    level: HighlightDisplayLevel,
    shortName: String,
    displayName: String,
    groupPath: Array<String>,
    groupDisplayName: String
  ): ExtensionPhase =
    extensionProvider(
      InspectionEP.GLOBAL_INSPECTION,
      inspection(inspectionTool, level, shortName, displayName, groupPath, groupDisplayName),
      LoadingOrder.FIRST
    )

  /**
   * registers a LocalInspection.
   * [LocalInspectionEP] is solely a wrapper over the generic [InspectionProfileEntry], which is a Subtype of both [GlobalInspectionTool] and [LocalInspectionTool].
   * @param groupDisplayName The displayed groupName in the user settings for Inspections.
   * @param groupPath The specified path where your Inspection is located. Use Strings without spacing.
   * @param shortName The displayed text, whenever [inspectionTool] is applicable.
   */
  fun MetaIde.addLocalInspection(
    inspectionTool: LocalInspectionTool,
    level: HighlightDisplayLevel,
    shortName: String,
    displayName: String,
    groupPath: Array<String>,
    groupDisplayName: String
  ): ExtensionPhase =
    extensionProvider(
      LocalInspectionEP.LOCAL_INSPECTION,
      localInspection(inspectionTool, level, shortName, displayName, groupPath, groupDisplayName),
      LoadingOrder.FIRST
    )

  /**
   * registers an [InspectionSuppressor] for the specified [PsiElement].
   * @sample [org.jetbrains.kotlin.idea.inspections.KotlinInspectionSuppressor]
   * TODO: Add a representation of [KotlinSuppressIntentionAction] with Meta
   */
  fun MetaIde.addInspectionSuppressor(
    suppressFor: (element: PsiElement, toolId: String) -> Boolean,
    suppressAction: (element: PsiElement?, toolId: String) -> List<SuppressQuickFix>
  ): ExtensionPhase =
    extensionProvider(
      LanguageInspectionSuppressors.INSTANCE,
      inspectionSuppressor(suppressFor, suppressAction)
    )

  fun InspectionSyntax.suppressQuickFix(
    name: String,
    familyName: String,
    applyFix: (project: Project, descriptor: ProblemDescriptor) -> Unit,
    isAvailable: (project: Project, context: PsiElement) -> Boolean,
    isSuppressAll: Boolean
  ): SuppressQuickFix =
    object : SuppressQuickFix {
      override fun getName(): String = name
      override fun getFamilyName(): String = familyName
      override fun applyFix(project: Project, descriptor: ProblemDescriptor): Unit = applyFix(project, descriptor)
      override fun isAvailable(project: Project, context: PsiElement): Boolean = isAvailable(project, context)
      override fun isSuppressAll(): Boolean = isSuppressAll
    }

  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> InspectionSyntax.applicableInspection(
    defaultFixText: String,
    kClass: Class<K> = KtElement::class.java as Class<K>,
    highlightingRange: (element: K) -> TextRange? = Noop.nullable1(),
    inspectionText: (element: K) -> String,
    applyTo: KtPsiFactory.(element: K, project: Project, editor: Editor?) -> Unit,
    isApplicable: (element: K) -> Boolean,
    inspectionHighlightType: (element: K) -> ProblemHighlightType =
      { _: K -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING },
    enabledByDefault: Boolean = true
  ): AbstractApplicabilityBasedInspection<K> =
    object : AbstractApplicabilityBasedInspection<K>(kClass) {
      override fun isEnabledByDefault(): Boolean = enabledByDefault
      override fun getShortName(): String = defaultFixText
      override val defaultFixText: String
        get() = defaultFixText

      override fun applyTo(element: K, project: Project, editor: Editor?): Unit =
        applyTo(project.ktPsiFactory, element, project, editor)

      override fun inspectionText(element: K): String =
        inspectionText(element)

      override fun isApplicable(element: K): Boolean =
        isApplicable(element)

      override fun inspectionHighlightRangeInElement(element: K): TextRange? =
        highlightingRange(element)

      override fun inspectionHighlightType(element: K): ProblemHighlightType =
        inspectionHighlightType(element)
    }

  fun InspectionSyntax.inspectionSuppressor(
    suppressFor: (element: PsiElement, toolId: String) -> Boolean,
    suppressAction: (element: PsiElement?, toolId: String) -> List<SuppressQuickFix>
  ): InspectionSuppressor =
    object : InspectionSuppressor {
      override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        suppressAction(element, toolId).toTypedArray()

      override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean =
        suppressFor(element, toolId)
    }

  fun InspectionSyntax.inspection(
    inspectionTool: GlobalInspectionTool,
    level: HighlightDisplayLevel,
    shortName: String,
    displayName: String,
    groupPath: Array<String>,
    groupDisplayName: String
  ): InspectionEP =
    object : InspectionEP() {
      override fun getDefaultLevel(): HighlightDisplayLevel = level
      override fun instantiateTool(): InspectionProfileEntry = inspectionTool
      override fun getShortName(): String = shortName
      override fun getDisplayName(): String = displayName
      override fun getGroupPath(): Array<String> = groupPath
      override fun getGroupDisplayName(): String = groupDisplayName
    }

  fun InspectionSyntax.localInspection(
    inspectionTool: LocalInspectionTool,
    level: HighlightDisplayLevel,
    shortName: String,
    displayName: String,
    groupPath: Array<String>,
    groupDisplayName: String
  ): LocalInspectionEP =
    object : LocalInspectionEP() {
      override fun getDefaultLevel(): HighlightDisplayLevel = level
      override fun instantiateTool(): InspectionProfileEntry = inspectionTool
      override fun getShortName(): String = shortName
      override fun getDisplayName(): String = displayName
      override fun getGroupPath(): Array<String> = groupPath
      override fun getGroupDisplayName(): String = groupDisplayName
    }
}
