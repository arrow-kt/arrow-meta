package arrow.meta.ide.plugins.nothing

import arrow.meta.Plugin
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.invoke
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.psi.codeStyle.DisplayPriority
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlighter
import org.jetbrains.kotlin.idea.highlighter.KotlinHighlightingColors

val IdeMetaPlugin.nothingIdePlugin: Plugin
  get() = "ColorSettingsPage" {
    meta(
      addColorSettingsPage(
        displayName = "MetaColorSettings1",
        priority = DisplayPriority.KEY_LANGUAGE_SETTINGS,
        language = KotlinLanguage.INSTANCE,
        highlighter = KotlinHighlighter(),
        demoText = """
          interface Mappable<F>
          val d = 7
          suspend fun hello(str: String): Unit = println(str)
          val help: Unit = hello(str = "help")
         """.trimIndent(),
        additionalHighlightingTags = mutableMapOf()
      ),
      addColorSettingsPage(
        displayName = "MetaColorSettings2",
        priority = DisplayPriority.KEY_LANGUAGE_SETTINGS,
        language = KotlinLanguage.INSTANCE,
        highlighter = KotlinHighlighter(),
        demoText = """
         interface <$Interface>Mappable</$Interface><F>
         val d = 7
         <$Keyword>suspend</$Keyword> fun hello(str: String): Unit = println(str)
         val help: Unit = hello(<$NamedArgument>str</$NamedArgument> = "hello")
        """.trimIndent(),
        additionalHighlightingTags = mutableMapOf(
          NamedArgument to KotlinHighlightingColors.NAMED_ARGUMENT,
          Interface to DefaultLanguageHighlighterColors.INTERFACE_NAME,
          Keyword to DefaultLanguageHighlighterColors.KEYWORD
        )
      ),
      addColorSettingsPage(
        displayName = "MetaColorSettings3",
        priority = DisplayPriority.KEY_LANGUAGE_SETTINGS,
        language = KotlinLanguage.INSTANCE,
        highlighter = KotlinHighlighter(),
        demoText = """
          interface <$Interface>Functor</$Interface><F>
          <$Keyword>extension</$Keyword> val semiSeven = 7.semigroup()
          fun <F> hello(ctx: Env<F> = <$Keyword>given</$Keyword>): Kind<F, Unit> =
            putStrLn(<$NamedArgument>str</$NamedArgument> = "hello")
          <$Keyword>remote</$Keyword> fun distributed(): IO<Response>
         """.trimIndent(),
        additionalHighlightingTags = mutableMapOf(
          NamedArgument to KotlinHighlightingColors.NAMED_ARGUMENT,
          Interface to DefaultLanguageHighlighterColors.INTERFACE_NAME,
          Keyword to DefaultLanguageHighlighterColors.KEYWORD
        )
      ),
      addColorSettingsPage(
        displayName = "PlainColorSettings",
        priority = DisplayPriority.LANGUAGE_SETTINGS,
        demoText = """
          <$Keyword>interface</$Keyword> <$Interface>Mappable</$Interface><F>
          <$Keyword>extension</$Keyword> val semiSeven = <$Number>7</$Number>.semigroup()
          <$Keyword>fun</$Keyword> hello(str: String): Unit = println(str)
          <$Keyword>extension</$Keyword> <$Keyword>val</$Keyword> help: Unit = hello(<$NamedArgument>str</$NamedArgument> = <$String>"hello"</$String>)
          <$Keyword>remote</$Keyword> <$Keyword>fun</$Keyword> distributed(): IO<Response>
        """.trimIndent(),
        additionalHighlightingTags = mutableMapOf(
          NamedArgument to KotlinHighlightingColors.NAMED_ARGUMENT,
          Interface to DefaultLanguageHighlighterColors.INTERFACE_NAME,
          Keyword to DefaultLanguageHighlighterColors.KEYWORD,
          Number to DefaultLanguageHighlighterColors.NUMBER,
          String to DefaultLanguageHighlighterColors.STRING
        )
      )
    )
  }

val Keyword: String = "keyword"
val Interface: String = "Interface"
val NamedArgument: String = "Named argument"
val Number: String = "Number"
val String: String = "String"