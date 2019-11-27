package arrow.meta.ide.dsl

import arrow.meta.ide.dsl.editor.action.AnActionSyntax
import arrow.meta.ide.dsl.editor.color.ColorSyntax
import arrow.meta.ide.dsl.editor.documentation.DocumentationProviderSyntax
import arrow.meta.ide.dsl.editor.hints.HintingSyntax
import arrow.meta.ide.dsl.editor.icon.IconProviderSyntax
import arrow.meta.ide.dsl.editor.inspection.InspectionSyntax
import arrow.meta.ide.dsl.editor.intention.IntentionExtensionProviderSyntax
import arrow.meta.ide.dsl.editor.language.LanguageSyntax
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import arrow.meta.ide.dsl.editor.liveTemplate.LiveTemplateSyntax
import arrow.meta.ide.dsl.editor.navigation.NavigationSyntax
import arrow.meta.ide.dsl.editor.refactoring.RefactoringSyntax
import arrow.meta.ide.dsl.editor.runLineMarker.RunLineMarkerSyntax
import arrow.meta.ide.dsl.editor.search.SearchSyntax
import arrow.meta.ide.dsl.editor.structureView.StructureViewSyntax
import arrow.meta.ide.dsl.editor.syntaxHighlighter.SyntaxHighlighterExtensionProviderSyntax
import arrow.meta.ide.dsl.editor.usage.UsageSyntax
import arrow.meta.ide.dsl.extensions.ExtensionProviderSyntax

interface IdeSyntax : IntentionExtensionProviderSyntax, IconProviderSyntax,
  SyntaxHighlighterExtensionProviderSyntax, InspectionSyntax, AnActionSyntax, ColorSyntax, HintingSyntax,
  LanguageSyntax, LineMarkerSyntax, LiveTemplateSyntax, NavigationSyntax, SearchSyntax, StructureViewSyntax,
  RefactoringSyntax, UsageSyntax, ExtensionProviderSyntax, RunLineMarkerSyntax, DocumentationProviderSyntax
