package arrow.meta.ide.dsl.editor.color

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.syntaxHighlighter.SyntaxHighlighterExtensionProviderSyntax
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.application.options.colors.InspectionColorSettingsPage
import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.options.colors.RainbowColorSettingsPage
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable
import com.intellij.ui.EditorCustomization
import org.jetbrains.kotlin.idea.KotlinLanguage
import javax.swing.Icon

/**
 *
 */
interface ColorSyntax {

  /**
   * registers a ColorSettingsPanel.
   */
  fun IdeMetaPlugin.addColorSettingsPage(
    displayName: String,
    demoText: String,
    highlighter: SyntaxHighlighter,
    priority: DisplayPriority,
    additionalHighlightingTagToDescriptorMap: MutableMap<String, TextAttributesKey>,
    icon: Icon? = null,
    attributesDescriptor: Array<AttributesDescriptor> = emptyArray(),
    colorDescriptor: Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY,
    isRainbowType: (type: TextAttributesKey) -> Boolean = Noop.boolean1False,
    language: Language? = KotlinLanguage.INSTANCE,
    customize: EditorEx.() -> Unit = Noop.effect1
  ): ExtensionPhase =
    extensionProvider(
      ColorSettingsPage.EP_NAME,
      colorSettingsPage(displayName, demoText, highlighter, priority, additionalHighlightingTagToDescriptorMap, icon, attributesDescriptor, colorDescriptor, isRainbowType, language, customize)
    )

  /**
   * @param attributesDescriptor use [toA]
   * @param highlighter use [SyntaxHighlighterExtensionProviderSyntax.syntaxHighlighter]
   * @param language the default is [KotlinLanguage].
   * @param customize add's customizations to the editor
   * @param isRainbowType the default doesn't display RainbowTypes for any Key.
   * @param priority use [DisplayPriority.KEY_LANGUAGE_SETTINGS] for programming languages and [DisplayPriority.LANGUAGE_SETTINGS] for less expressive languages like `JSON`.
   * @param priority [DisplayPriority.COMMON_SETTINGS] is used for generic setting's like Debugger's. Check [DisplayPriority] for more information.
   */
  fun ColorSyntax.colorSettingsPage(
    displayName: String,
    demoText: String,
    highlighter: SyntaxHighlighter,
    priority: DisplayPriority,
    additionalHighlightingTagToDescriptorMap: MutableMap<String, TextAttributesKey>,
    icon: Icon? = null,
    attributesDescriptor: Array<AttributesDescriptor> = emptyArray(),
    colorDescriptor: Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY,
    isRainbowType: (type: TextAttributesKey) -> Boolean = Noop.boolean1False,
    language: Language? = KotlinLanguage.INSTANCE,
    customize: EditorEx.() -> Unit = Noop.effect1
  ): ColorSettingsPage =
    object : ColorSettingsPage, RainbowColorSettingsPage, InspectionColorSettingsPage, DisplayPrioritySortable, EditorCustomization {
      // TODO: investigate [com.intellij.openapi.vcs.ui.CommitMessage.InspectionCustomization] for a better API regarding [customize]
      // TODO: [com.intellij.ui.SimpleEditorCustomization] can be used for UI

      override fun getHighlighter(): SyntaxHighlighter = highlighter
      override fun getIcon(): Icon? = icon
      override fun getAttributeDescriptors(): Array<AttributesDescriptor> = attributesDescriptor
      override fun getColorDescriptors(): Array<ColorDescriptor> = colorDescriptor
      override fun getDisplayName(): String = displayName
      override fun getDemoText(): String = demoText
      override fun isRainbowType(type: TextAttributesKey?): Boolean = type?.let(isRainbowType) ?: false
      override fun getLanguage(): Language? = language
      override fun getPriority(): DisplayPriority = priority
      override fun customize(editor: EditorEx): Unit = customize(editor)
      override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey> =
        additionalHighlightingTagToDescriptorMap
    }

  infix fun String.toA(key: TextAttributesKey): AttributesDescriptor = AttributesDescriptor(this, key)
}
