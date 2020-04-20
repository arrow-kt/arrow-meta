package arrow.meta.ide.dsl

import arrow.meta.ide.dsl.application.ApplicationSyntax
import arrow.meta.ide.dsl.editor.action.AnActionSyntax
import arrow.meta.ide.dsl.editor.annotator.AnnotatorSyntax
import arrow.meta.ide.dsl.editor.color.ColorSettingsSyntax
import arrow.meta.ide.dsl.editor.documentation.DocumentationSyntax
import arrow.meta.ide.dsl.editor.goto.GotoRelatedSyntax
import arrow.meta.ide.dsl.editor.hints.HintingSyntax
import arrow.meta.ide.dsl.editor.icon.IconProviderSyntax
import arrow.meta.ide.dsl.editor.inspection.InspectionSyntax
import arrow.meta.ide.dsl.editor.kotlinextensions.KotlinExtensionSyntax
import arrow.meta.ide.dsl.editor.language.LanguageSyntax
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import arrow.meta.ide.dsl.editor.liveTemplate.LiveTemplateSyntax
import arrow.meta.ide.dsl.editor.navigation.NavigationSyntax
import arrow.meta.ide.dsl.editor.parser.ParserSyntax
import arrow.meta.ide.dsl.editor.search.SearchSyntax
import arrow.meta.ide.dsl.editor.structureView.StructureViewSyntax
import arrow.meta.ide.dsl.editor.syntaxHighlighter.SyntaxHighlighterSyntax
import arrow.meta.ide.dsl.editor.usage.UsageSyntax
import arrow.meta.ide.dsl.extensions.ExtensionProviderSyntax
import arrow.meta.ide.dsl.resolve.ResolveScopeSyntax
import arrow.meta.ide.dsl.resolve.ResolveProviderSyntax
import arrow.meta.ide.dsl.ui.dialogs.DialogSyntax
import arrow.meta.ide.dsl.ui.notification.NotificationSyntax
import arrow.meta.ide.dsl.ui.popups.PopupSyntax
import arrow.meta.ide.dsl.ui.toolwindow.ToolWindowSyntax

interface IdeSyntax : IconProviderSyntax,
  SyntaxHighlighterSyntax, InspectionSyntax, AnActionSyntax, ColorSettingsSyntax, HintingSyntax,
  LanguageSyntax, LineMarkerSyntax, LiveTemplateSyntax, NavigationSyntax, SearchSyntax, StructureViewSyntax,
  UsageSyntax, ExtensionProviderSyntax, DocumentationSyntax, KotlinExtensionSyntax, DialogSyntax, PopupSyntax,
  NotificationSyntax, ToolWindowSyntax, GotoRelatedSyntax, AnnotatorSyntax, ParserSyntax, ApplicationSyntax,
  ResolveScopeSyntax, ResolveProviderSyntax