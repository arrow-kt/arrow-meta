package arrow.meta.ide.dsl.editor.references

import arrow.meta.ide.MetaIde
import arrow.meta.ide.phases.editor.references.ReferenceProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.lang.Language
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.IElementTypePattern
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.PsiFilePattern
import com.intellij.patterns.VirtualFilePattern
import com.intellij.pom.PomTarget
import com.intellij.pom.PomTargetPsiElement
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.tree.IElementType
import com.intellij.util.ProcessingContext

interface ReferenceSyntax {

  fun <A : PsiElement> MetaIde.addPsiRefContributor(
    lang: Language = Language.ANY,
    pattern: ElementPattern<out A>,
    references: PsiElement.(ctx: ProcessingContext) -> List<PsiReference> = Noop.emptyList2(),
    priority: Double = PsiReferenceRegistrar.DEFAULT_PRIORITY
  ): ExtensionPhase =
    refContributor(lang) {
      register(pattern, references, priority)
    }

  @Suppress("UNCHECKED_CAST")
  fun <A : PsiElement> MetaIde.addRefContributor(
    lang: Language = Language.ANY,
    pattern: ElementPattern<out A>,
    references: A.(ctx: ProcessingContext) -> List<PsiReference> = Noop.emptyList2(),
    priority: Double = PsiReferenceRegistrar.DEFAULT_PRIORITY
  ): ExtensionPhase =
    addPsiRefContributor(lang, pattern, { (this as? A)?.let { a: A -> references(a, it) }.orEmpty() }, priority)

  fun ReferenceSyntax.refContributor(
    lang: Language = Language.ANY,
    f: PsiReferenceRegistrar.() -> Unit = Noop.effect1
  ): ExtensionPhase =
    ReferenceProvider.RegisterReferenceContributor(lang, contributor(f))

  fun ReferenceSyntax.contributor(f: PsiReferenceRegistrar.() -> Unit = Noop.effect1): PsiReferenceContributor =
    object : PsiReferenceContributor() {
      override fun registerReferenceProviders(registrar: PsiReferenceRegistrar): Unit =
        f(registrar)
    }

  fun <A : PsiElement> PsiReferenceRegistrar.register(
    pattern: ElementPattern<out A>,
    references: PsiElement.(ctx: ProcessingContext) -> List<PsiReference> = Noop.emptyList2(),
    priority: Double = PsiReferenceRegistrar.DEFAULT_PRIORITY
  ): Unit =
    registerReferenceProvider(
      pattern,
      provider { ctx ->
        references(this, ctx)
      },
      priority
    )

  @Suppress("UNCHECKED_CAST")
  fun <A : PsiElement> ReferenceSyntax.psiElement(on: Class<A> = PsiElement::class.java as Class<A>): PsiElementPattern.Capture<A> =
    PlatformPatterns.psiElement(on)

  fun <A : IElementType> ReferenceSyntax.psiElement(on: A): PsiElementPattern.Capture<PsiElement> =
    PlatformPatterns.psiElement(on)

  fun ReferenceSyntax.psiComment(): PsiElementPattern.Capture<PsiComment> =
    PlatformPatterns.psiComment()

  @Suppress("UNCHECKED_CAST")
  fun <A : PsiFile> ReferenceSyntax.psiFile(on: Class<A> = PsiFile::class.java as Class<A>): PsiFilePattern.Capture<A> =
    PlatformPatterns.psiFile(on)

  fun ReferenceSyntax.virtualFile(): VirtualFilePattern =
    PlatformPatterns.virtualFile()

  fun ReferenceSyntax.elementType(): IElementTypePattern =
    PlatformPatterns.elementType()

  fun <A : PomTarget> ReferenceSyntax.pomElement(target: ElementPattern<out A>): PsiElementPattern.Capture<PomTargetPsiElement> =
    PlatformPatterns.pomElement(target)

  fun <A> ReferenceSyntax.transform(
    debugName: String,
    transform: A.(ctx: ProcessingContext?) -> A? = Noop.nullable2()
  ): PatternCondition<A> =
    condition(debugName) { ctx: ProcessingContext? ->
      transform(this, ctx)?.run { true } ?: false
    }

  fun <A> ReferenceSyntax.condition(
    debugName: String,
    condition: A.(ctx: ProcessingContext?) -> Boolean = Noop.boolean2False
  ): PatternCondition<A> =
    object : PatternCondition<A>(debugName) {
      override fun accepts(t: A, context: ProcessingContext?): Boolean =
        condition(t, context)
    }

  fun ReferenceSyntax.provider(
    references: PsiElement.(ctx: ProcessingContext) -> List<PsiReference> = Noop.emptyList2()
  ): PsiReferenceProvider =
    object : PsiReferenceProvider() {
      override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> =
        references(element, context).toTypedArray()
    }
}