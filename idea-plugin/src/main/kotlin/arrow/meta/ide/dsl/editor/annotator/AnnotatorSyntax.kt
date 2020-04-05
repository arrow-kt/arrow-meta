package arrow.meta.ide.dsl.editor.annotator

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.Language
import com.intellij.lang.annotation.Annotator
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.idea.quickfix.QuickFixActionBase
import org.jetbrains.kotlin.idea.quickfix.KotlinSingleIntentionActionFactory

interface AnnotatorSyntax {
  // TODO: consider [com.intellij.codeInsight.navigation.NavigationGutterIconBuilder.install(com.intellij.lang.annotation.AnnotationHolder, com.intellij.psi.PsiElement)

  /**
   * registers an Annotator for [lang].
   * Annotators add language specific annotations to a file.
   * Keep in mind that there might be multiple instances of the same Annotator at runtime.
   * @see Annotator
   * @see KotlinSingleIntentionActionFactory and all its Subtypes for examples
   * @see QuickFixActionBase and all its Subtypes for [action] or [actionsForAll]
   * ```kotlin:ank
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.plugins.purity.isImpure
   * import arrow.meta.ide.invoke
   * import com.intellij.lang.annotation.Annotator
   * import org.jetbrains.kotlin.idea.quickfix.AddModifierFix
   * import org.jetbrains.kotlin.lexer.KtTokens
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * val IdeMetaPlugin.purityAnnotator: IdePlugin
   *   get() = "PurityCheck Annotator" {
   *     meta(
   *       addAnnotator(
   *         annotator = Annotator { element, holder -> // in some situations there are 2 or more error annotations
   *           element.safeAs<KtNamedFunction>()?.takeIf { it.isImpure }?.let { f ->
   *             holder.createErrorAnnotation(f, "Function is impure")?.let { error ->
   *               error.registerUniversalFix(AddModifierFix(f, KtTokens.SUSPEND_KEYWORD), f.identifyingElement?.textRange, null)
   *             }
   *           }
   *         }
   *       )
   *     )
   *   }
   * ```
   */
  fun IdeMetaPlugin.addAnnotator(lang: Language = KotlinLanguage.INSTANCE, annotator: Annotator): ExtensionPhase =
    ExtensionProvider.AddLanguageAnnotator(lang, annotator)
}