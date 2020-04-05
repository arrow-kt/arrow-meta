package arrow.meta.ide.dsl.editor.color

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.syntaxHighlighter.SyntaxHighlighterSyntax
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
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import javax.swing.Icon
import com.intellij.lang.annotation.Annotator

/**
 * [ColorSettingsPage] goes hand in hand with [SyntaxHighlighter]'s.
 * [ColorSettingsPage] add's a custom page in the user Settings under "Color Scheme" and is based on a custom [SyntaxHighlighter] composed with [SyntaxHighlighterSyntax.syntaxHighlighter].
 * Consequently, `ColorSettingsPages` not only allow users to customize the colors of [SyntaxHighlighter]'s for a better ide experience,
 * More importantly, they provide means, to highlight additional descriptors from the `Parser` or [Annotator].
 * One use-case for `ColorSettingsPages`, among others, is to use them as a visual template in the ide, before the actual [SyntaxHighlighter] is created.
 * Therefore, a [ColorSettingsPage] visually enhances the underlying [SyntaxHighlighter] and all descriptors from the `Parser` and [Annotator].
 * Additionally, there are other use-cases with `Themes`.
 * @see SyntaxHighlighterSyntax
 */
interface ColorSettingsSyntax {
  // TODO("add `toColorSettingsPage` from a SyntaxHighlighter")

  /**
   * This extension registers a [ColorSettingsPage].
   * Let's register `MetaColorSettings` with the [KotlinHighlighter] and an empty [additionalHighlightingTags].
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.psi.codeStyle.DisplayPriority
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter
   * import org.jetbrains.kotlin.idea.KotlinLanguage
   *
   * //sampleStart
   * val IdeMetaPlugin.syntaxHighlighter: IdePlugin
   *  get() = "ColorSettingsPage for MetaSyntaxHighlighter" {
   *   meta(
   *    addColorSettingsPage(
   *     displayName = "MetaColorSettings",
   *     priority = DisplayPriority.KEY_LANGUAGE_SETTINGS,
   *     language = KotlinLanguage.INSTANCE,
   *     highlighter = KotlinHighlighter(),
   *     demoText = """
   *       interface Mappable<F>
   *       val d = 7
   *       suspend fun hello(str: String): Unit = println(str)
   *       val help: Unit = hello(str = "help")
   *      """.trimIndent(),
   *     additionalHighlightingTags = mutableMapOf()
   *    )
   *   )
   *  }
   *  //sampleEnd
   * ```
   * Even though, [additionalHighlightingTags] is empty the standard Kotlin `Keywords`: `fun`, `interface` and `val` are highlighted.
   * That is evident, due to the underlying `Lexer` in [KotlinHighlighter], the ide registers generated Tokens from `Lexers` automatically and applies it to the [demoText].
   * Though, most of the time a rich `SyntaxHighlighter` is not sufficient enough to highlight the whole scope of the [demoText].
   * As, there are descriptors and tokens of the language, which are processed or generated with the `Parser` or [Annotator].
   * Here is where [ColorSettingsPage] shines, it allow's the ide to register and highlight those elements, too.
   * @see org.jetbrains.kotlin.lexer.KtTokens
   * Nonetheless, the `suspend` Keyword is not included in the `Lexer` of [KotlinHighlighter], nor is it tagged in [demoText] therefore `suspend`, the interface identifier and `Named arguments` are not highlighted.
   * ---
   * Adding `KeyWords`, `Interface` and `Named Arguments` as tags to [demoText] is not enough.
   * They have to be added to [additionalHighlightingTags] in order to be indexed, by the ide.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.psi.codeStyle.DisplayPriority
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter
   * import org.jetbrains.kotlin.idea.KotlinLanguage
   *
   * //sampleStart
   * val IdeMetaPlugin.syntaxHighlighter: IdePlugin
   *  get() = "ColorSettingsPage for MetaSyntaxHighlighter" {
   *   meta(
   *    addColorSettingsPage(
   *     displayName = "MetaColorSettings",
   *     priority = DisplayPriority.KEY_LANGUAGE_SETTINGS,
   *     language = KotlinLanguage.INSTANCE,
   *     highlighter = KotlinHighlighter(),
   *     demoText = """
   *      interface <$Interface>Mappable</$Interface><F>
   *      val d = 7
   *      <$Keyword>suspend</$Keyword> fun hello(str: String): Unit = println(str)
   *      val help: Unit = hello(<$NamedArgument>str</$NamedArgument> = "hello")
   *     """.trimIndent(),
   *     additionalHighlightingTags = mutableMapOf(
   *      NamedArgument to KotlinHighlightingColors.NAMED_ARGUMENT,
   *      Interface to DefaultLanguageHighlighterColors.INTERFACE_NAME,
   *      Keyword to DefaultLanguageHighlighterColors.KEYWORD
   *     )
   *    )
   *   )
   *  }
   *
   * val Keyword: String = "keyword"
   * val Interface: String = "Interface"
   * val NamedArgument: String = "Named argument"
   *  //sampleEnd
   * ```
   *
   * [ColorSettingsPage] does not register tagged Tokens in [demoText] to the Language, for that we need other `Extensions`.
   * Nonetheless, we can register tokens to be highlighted, assuming we already provide an ide extension instance with these added tokens - mainly with a `Parser` and [Annotator].
   *
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.psi.codeStyle.DisplayPriority
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter
   * import org.jetbrains.kotlin.idea.KotlinLanguage
   *
   * val Keyword: String = "keyword"
   * val Interface: String = "Interface"
   * val NamedArgument: String = "Named argument"
   * //sampleStart
   * val IdeMetaPlugin.syntaxHighlighter: IdePlugin
   *  get() = "ColorSettingsPage for MetaSyntaxHighlighter" {
   *   meta(
   *    addColorSettingsPage(
   *     displayName = "MetaColorSettings",
   *     priority = DisplayPriority.KEY_LANGUAGE_SETTINGS,
   *     language = KotlinLanguage.INSTANCE,
   *     highlighter = KotlinHighlighter(),
   *     demoText = """
   *       interface <$Interface>Functor</$Interface><F>
   *       <$Keyword>extension</$Keyword> val semiSeven = 7.semigroup()
   *       fun <F> hello(ctx: Env<F> = <$Keyword>given</$Keyword>): Kind<F, Unit> =
   *         putStrLn(<$NamedArgument>str</$NamedArgument> = "hello")
   *       <$Keyword>remote</$Keyword> fun distributed(): IO<Response>
   *      """.trimIndent(),,
   *     additionalHighlightingTags = mutableMapOf(
   *      NamedArgument to KotlinHighlightingColors.NAMED_ARGUMENT,
   *      Interface to DefaultLanguageHighlighterColors.INTERFACE_NAME,
   *      Keyword to DefaultLanguageHighlighterColors.KEYWORD
   *     )
   *    )
   *   )
   *  }
   *  //sampleEnd
   * ```
   *
   * We can achieve a similar visual representation with an empty instance [PlainSyntaxHighlighter] - which is the default for [highlighter].
   *
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.invoke
   * import com.intellij.psi.codeStyle.DisplayPriority
   * import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors
   *
   * val Keyword: String = "keyword"
   * val Interface: String = "Interface"
   * val NamedArgument: String = "Named argument"
   * val Number: String = "Number"
   * val String: String = "String"
   *
   * //sampleStart
   * val IdeMetaPlugin.syntaxHighlighter: IdePlugin
   *  get() = "Plain ColorSettingsPage" {
   *   meta(
   *    addColorSettingsPage(
   *     displayName = "PlainColorSettings",
   *     priority = DisplayPriority.LANGUAGE_SETTINGS,
   *     demoText = """
   *      <$Keyword>interface</$Keyword> <$Interface>Mappable</$Interface><F>
   *      <$Keyword>extension</$Keyword> <$Keyword>val</$Keyword> semiSeven = <$Number>7</$Number>.semigroup()
   *      <$Keyword>fun</$Keyword> hello(str: String): Unit = println(str)
   *      <$Keyword>extension</$Keyword> <$Keyword>val</$Keyword> help: Unit = hello(<$NamedArgument>str</$NamedArgument> = <$String>"hello"</$String>)
   *      <$Keyword>remote</$Keyword> <$Keyword>fun</$Keyword> distributed(): IO<Response>
   *     """.trimIndent(),
   *     additionalHighlightingTags = mutableMapOf(
   *      NamedArgument to KotlinHighlightingColors.NAMED_ARGUMENT,
   *      Interface to DefaultLanguageHighlighterColors.INTERFACE_NAME,
   *      Keyword to DefaultLanguageHighlighterColors.KEYWORD,
   *      Number to DefaultLanguageHighlighterColors.NUMBER,
   *      String to DefaultLanguageHighlighterColors.STRING
   *     )
   *    )
   *   )
   *  }
   *  //sampleEnd
   * ```
   * Ideally, constructing a [ColorSettingsPage] from a [SyntaxHighlighter], `Parser` and [Annotator] should be fairly linear as those define a Mappings between `Tokens` and `TextAttributes`, for instance in [SyntaxHighlighter.getTokenHighlights].
   * To conclude, [ColorSettingsPage] improves upon the underlying `Lexer` of an [SyntaxHighlighter], in a way to use [TextAttributesKey]s as tags, the ability to manipulate the ide and index Tokens, to be later then processed by other `Extensions`.
   * @see [ColorSettingsPage]
   * @see [colorSettingsPage]
   * @see TextAttributesKey
   * @see DefaultLanguageHighlighterColors
   * @param highlighter an empty default instance is [PlainSyntaxHighlighter]
   * @sample [KotlinColorSettingsPage]
   */
  fun IdeMetaPlugin.addColorSettingsPage(
    displayName: String,
    priority: DisplayPriority,
    additionalHighlightingTags: MutableMap<String, TextAttributesKey>,
    attributesDescriptor: Array<AttributesDescriptor> =
      additionalHighlightingTags.map { (k, v) -> k toA v }.toTypedArray(),
    demoText: String = FontEditorPreview.getIDEDemoText(),
    highlighter: SyntaxHighlighter = PlainSyntaxHighlighter(),
    icon: Icon? = null,
    colorDescriptor: Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY,
    isRainbowType: (type: TextAttributesKey) -> Boolean = Noop.boolean1False,
    language: Language? = null,
    customize: EditorEx.() -> Unit = Noop.effect1
  ): ExtensionPhase =
    extensionProvider(
      ColorSettingsPage.EP_NAME,
      colorSettingsPage(displayName, priority, additionalHighlightingTags, attributesDescriptor, demoText, highlighter, icon, colorDescriptor, isRainbowType, language, customize)
    )

  /**
   * @param attributesDescriptor use [toA] to construct the Array
   * @param highlighter use [SyntaxHighlighterSyntax.syntaxHighlighter]. The default is for Kotlin.
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
    additionalHighlightingTags: MutableMap<String, TextAttributesKey>,
    attributesDescriptor: Array<AttributesDescriptor> =
      additionalHighlightingTags.map { (k, v) -> k toA v }.toTypedArray(),
    demoText: String = FontEditorPreview.getIDEDemoText(),
    highlighter: SyntaxHighlighter = PlainSyntaxHighlighter(),
    icon: Icon? = null,
    colorDescriptor: Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY,
    isRainbowType: (type: TextAttributesKey) -> Boolean = Noop.boolean1False,
    language: Language? = null,
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
        additionalHighlightingTags
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
