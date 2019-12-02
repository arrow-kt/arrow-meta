package arrow.meta.ide.dsl.editor.color

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.syntaxHighlighter.SyntaxHighlighterExtensionProviderSyntax
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.application.options.colors.FontEditorPreview
import com.intellij.application.options.colors.InspectionColorSettingsPage
import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.PlainSyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.openapi.options.colors.RainbowColorSettingsPage
import com.intellij.psi.codeStyle.DisplayPriority
import com.intellij.psi.codeStyle.DisplayPrioritySortable
import com.intellij.ui.EditorCustomization
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.highlighter.KotlinColorSettingsPage
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter
import javax.swing.Icon

/**
 * [ColorSettingsPage] goes hand in hand with [SyntaxHighlighter]'s.
 * [ColorSettingsPage] add's a costume page in the user Settings under "Color and Fonts" and is based on a costume [SyntaxHighlighter] composed with [SyntaxHighlighterExtensionProviderSyntax.syntaxHighlighter].
 * Consequently, plugin developer's may refine [SyntaxHighlighter]'s with a [ColorSettingsPage] for a better ide experience.
 * Hence, a [ColorSettingsPage] may act as a visual template for the SyntaxHighlighter.
 */
interface ColorSettingsSyntax {
  // TODO("add `toColorSettingsPage` from a SyntaxHighlighter")
  // TODO: Add an example

  /**
   * registers a ColorSettingsPanel.
   * The following example will add `KeyWords`, `Numbers` and `Modifiers` to the list of attributeDescriptors.
   * Ideally, constructing a [ColorSettingsPage] from a [SyntaxHighlighter] should be fairly linear as the latter already defines a Mapping between `Tokens` and `TextAttributes` in [SyntaxHighlighter.getTokenHighlights].
   * [ColorSettingsPage] reuses the same logic other than to name each [TextAttributesKey] and the ability to manipulate the Editor.
   * @see [ColorSettingsPage]
   * @see [colorSettingsPage]
   * @param highlighter an empty default instance is [PlainSyntaxHighlighter]
   * @sample [KotlinColorSettingsPage]
   */
  fun IdeMetaPlugin.addColorSettingsPage(
    displayName: String,
    priority: DisplayPriority,
    additionalHighlightingTagToDescriptorMap: MutableMap<String, TextAttributesKey>,
    attributesDescriptor: Array<AttributesDescriptor>,
    demoText: String = FontEditorPreview.getIDEDemoText(),
    highlighter: SyntaxHighlighter = KotlinHighlighter(),
    icon: Icon? = null,
    colorDescriptor: Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY,
    isRainbowType: (type: TextAttributesKey) -> Boolean = Noop.boolean1False,
    language: Language? = KotlinLanguage.INSTANCE,
    customize: EditorEx.() -> Unit = Noop.effect1
  ): ExtensionPhase =
    extensionProvider(
      ColorSettingsPage.EP_NAME,
      colorSettingsPage(displayName, priority, additionalHighlightingTagToDescriptorMap, attributesDescriptor, demoText, highlighter, icon, colorDescriptor, isRainbowType, language, customize)
    )

  /**
   * @param attributesDescriptor use [toA] to construct the Array
   * @param highlighter use [SyntaxHighlighterExtensionProviderSyntax.syntaxHighlighter]. The default is for Kotlin.
   * @param language the default is [KotlinLanguage].
   * @param customize add's customizations to the editor
   * @param isRainbowType RainbowTypes add color changes to specified [TextAttributesKey]s. The default doesn't display RainbowTypes for any Key. For example Kotlin and Java define local Variables and Parameter's as RainbowTypes.
   * @param priority use [DisplayPriority.KEY_LANGUAGE_SETTINGS] for programming languages and [DisplayPriority.LANGUAGE_SETTINGS] for less expressive languages like `JSON`.
   * @param priority [DisplayPriority.COMMON_SETTINGS] is used for generic setting's like Debugger's. Check [DisplayPriority] for more information.
   * @see addColorSettingsPage
   */
  fun ColorSettingsSyntax.colorSettingsPage(
    displayName: String,
    priority: DisplayPriority,
    additionalHighlightingTagToDescriptorMap: MutableMap<String, TextAttributesKey>,
    attributesDescriptor: Array<AttributesDescriptor>,
    demoText: String = FontEditorPreview.getIDEDemoText(),
    highlighter: SyntaxHighlighter = KotlinHighlighter(),
    icon: Icon? = null,
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

  /**
   * convenience function to create [AttributesDescriptor]
   * @receiver is the displayName
   */
  infix fun String.toA(key: TextAttributesKey): AttributesDescriptor = AttributesDescriptor(this, key)

  /**
   * convenience extension to create [AttributesDescriptor] using [TextAttributesKey.getExternalName] as it's displayName
   */
  val TextAttributesKey.descriptor: AttributesDescriptor
    get() = externalName toA this
}
