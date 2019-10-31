package arrow.meta.dsl.ide

import arrow.meta.dsl.ide.editor.action.AnActionSyntax
import arrow.meta.dsl.ide.editor.color.ColorSyntax
import arrow.meta.dsl.ide.editor.hints.HintingSyntax
import arrow.meta.dsl.ide.editor.icon.IconProviderSyntax
import arrow.meta.dsl.ide.editor.inspection.InspectionSyntax
import arrow.meta.dsl.ide.editor.intention.IntentionExtensionProviderSyntax
import arrow.meta.dsl.ide.editor.language.LanguageSyntax
import arrow.meta.dsl.ide.editor.lineMarker.LineMarkerSyntax
import arrow.meta.dsl.ide.editor.liveTemplate.LiveTemplateSyntax
import arrow.meta.dsl.ide.editor.navigation.NavigationSyntax
import arrow.meta.dsl.ide.editor.refactoring.RefactoringSyntax
import arrow.meta.dsl.ide.editor.search.SearchSyntax
import arrow.meta.dsl.ide.editor.structureView.StructureViewSyntax
import arrow.meta.dsl.ide.editor.syntaxHighlighter.SyntaxHighlighterExtensionProviderSyntax
import arrow.meta.dsl.ide.editor.usage.UsageSyntax
import arrow.meta.dsl.ide.extensions.ExtensionProviderSyntax

/**
 * The IDE DSL empowers library and compiler plugin authors to bring their features closer to the development experience.
 * Arrow Meta allows sharing the compiler plugin code with the IDE code so developers can reuse their compiler plugin
 * functions in their IDE plugin.
 *
 * The Arrow Meta IDE DSL models the entire set of interesting features the Kotlin IDE plugin offers and the IDEA plugin
 * system exposes to interface with the editor.
 */
interface IdeSyntax : IntentionExtensionProviderSyntax, IconProviderSyntax,
  SyntaxHighlighterExtensionProviderSyntax, InspectionSyntax, AnActionSyntax, ColorSyntax, HintingSyntax,
  LanguageSyntax, LineMarkerSyntax, LiveTemplateSyntax, NavigationSyntax, SearchSyntax, StructureViewSyntax,
  RefactoringSyntax, UsageSyntax, ExtensionProviderSyntax
