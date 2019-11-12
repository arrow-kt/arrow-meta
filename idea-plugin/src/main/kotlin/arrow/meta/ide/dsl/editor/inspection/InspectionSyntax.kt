package arrow.meta.ide.dsl.editor.inspection

import arrow.meta.ide.IdeMetaPlugin
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
import org.jetbrains.kotlin.idea.inspections.AbstractKotlinInspection
import org.jetbrains.kotlin.idea.quickfix.KotlinSuppressIntentionAction
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * More General Inspections can be build with [AbstractKotlinInspection] e.g.: [org.jetbrains.kotlin.idea.inspections.RedundantSuspendModifierInspection]
 */
interface InspectionSyntax : InspectionUtilitySyntax {
  @Suppress("UNCHECKED_CAST")
  fun <K : KtElement> IdeMetaPlugin.addApplicableInspection(
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
      level,
      defaultFixText,
      defaultFixText,
      groupPath,
      defaultFixText
    )

  fun IdeMetaPlugin.addGlobalInspection(
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

  fun IdeMetaPlugin.addLocalInspection(
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
   * suppresses Warning[org.jetbrains.kotlin.idea.inspections.KotlinInspectionSuppressor]
   * TODO: Add a representation of [KotlinSuppressIntentionAction]
   */
  fun IdeMetaPlugin.addInspectionSuppressor(
    suppressFor: (element: PsiElement, toolId: String) -> Boolean,
    suppressAction: (element: PsiElement?, toolId: String) -> Array<SuppressQuickFix>
  ): ExtensionPhase =
    extensionProvider(
      LanguageInspectionSuppressors.INSTANCE,
      inspectionSuppressor(suppressFor, suppressAction)
    )

  fun InspectionSyntax.supressQuickFix(
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
      { _ -> ProblemHighlightType.GENERIC_ERROR_OR_WARNING },
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
    suppressAction: (element: PsiElement?, toolId: String) -> Array<SuppressQuickFix>
  ): InspectionSuppressor =
    object : InspectionSuppressor {
      override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        suppressAction(element, toolId)

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
