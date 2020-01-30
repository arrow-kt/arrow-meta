package arrow.meta.ide.dsl.extensions

import arrow.meta.dsl.analysis.AnalysisSyntax
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.dsl.editor.icon.IconProviderSyntax
import arrow.meta.ide.dsl.editor.inspection.InspectionSyntax
import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
import arrow.meta.ide.dsl.editor.search.SearchSyntax
import arrow.meta.ide.phases.editor.extension.ExtensionProvider
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.ContainerProvider
import com.intellij.ide.IconProvider
import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.fileTypes.FileTypeExtension
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.ClassExtension
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor

/**
 * The [ExtensionProvider] phase sits at the core of the main API in IntelliJ's Platform for ExtensionPoints.
 * ExtensionPoints regardless of their nature are means to interact with the ide.
 * The [ExtensionProviderSyntax] allows plugin developers to define, compose and manipulate workflow's in the ide environment explicitly without specifying those changes in the `plugin.xml`.
 * The latter is true for all Jetbrains products, which are based on the IntelliJ Platform API, e.g.: `Rider`, `MPS`, `CLion`, etc.
 * It's polymorphic shape facilitates existing and newly created Extensions, for example:
 * [LanguageExtension](https://github.com/JetBrains/intellij-community/blob/master/platform/platform-resources/src/META-INF/LangExtensionPoints.xml),
 * [PlatformExtensions](https://github.com/JetBrains/intellij-community/blob/master/platform/platform-resources/src/META-INF/PlatformExtensionPoints.xml),
 * [RefactoringExtensions](https://github.com/JetBrains/intellij-community/blob/master/platform/platform-resources/src/META-INF/RefactoringExtensionPoints.xml) and many more.
 */
interface ExtensionProviderSyntax {
  // TODO: provide complementary methods to integrate [E] with the editor. This requires further integrations to internals
  // TODO: Check out [org.jetbrains.kotlin.resolve.checkers.PlatformDiagnosticSuppressor] for further improvements in [addDiagnosticSuppressor]

  /**
   * The [extensionProvider] function registers a concrete implementation for `Extensions` with an [ExtensionPointName].
   * Or builds higher-level API's such as [IconProviderSyntax.addIcon], which registers a FileIcon to a File and StructureView using [IconProvider.EXTENSION_POINT_NAME].
   * ```kotlin:ank
   * import arrow.meta.internal.Noop
   * import arrow.meta.phases.ExtensionPhase
   * import arrow.meta.ide.IdeMetaPlugin
   * import com.intellij.ide.IconProvider
   * import com.intellij.openapi.extensions.LoadingOrder
   * import com.intellij.openapi.project.DumbAware
   * import com.intellij.psi.PsiElement
   * import javax.swing.Icon
   *
   * //sampleStart
   * fun <A : PsiElement> IdeMetaPlugin.addIcon(
   *   icon: Icon? = null,
   *   transform: (psiElement: PsiElement, flag: Int) -> A? = Noop.nullable2()
   *  ): ExtensionPhase =
   *   extensionProvider(
   *     IconProvider.EXTENSION_POINT_NAME,
   *     object : IconProvider(), DumbAware {
   *     // [DumbAware] signifies that this implementation is available when the editor performs an index update.
   *       override fun getIcon(p0: PsiElement, p1: Int): Icon? =
   *         transform(p0, p1)?.run { icon }
   *      },
   *     LoadingOrder.FIRST
   *   )
   * //sampleEnd
   * ```
   * More importantly, using [extensionProvider] and all its variations lifts any ide workflow for `Extension's` to `Meta` and is evident
   * to all derived instances like [LineMarkerSyntax], [InspectionSyntax], [SearchSyntax] and many more.
   * Hence, if a costum workflow, doesn't exist in `Meta`, using the aforementioned technique does so. We're always open for PR's to extend `Meta`.
   * @param impl is the concrete implementation
   * @param loadingOrder has to be set as [LoadingOrder.FIRST], whenever we introduce visual changes
   * @see ExtensionProviderSyntax
   */
  fun <E> IdeMetaPlugin.extensionProvider(
    EP_NAME: ExtensionPointName<E>,
    impl: E,
    loadingOrder: LoadingOrder = LoadingOrder.ANY // Todo: Check LoadingOrder
  ): ExtensionPhase =
    ExtensionProvider.AddExtension(EP_NAME, impl, loadingOrder)

  /**
   * The [extensionProvider] extension registers a concrete implementation for [LanguageExtension]'s.
   * @see [extensionProvider] KDoc's
   */
  fun <E> IdeMetaPlugin.extensionProvider(
    LE: LanguageExtension<E>,
    impl: E,
    lang: Language = KotlinLanguage.INSTANCE
  ): ExtensionPhase =
    ExtensionProvider.AddLanguageExtension(LE, impl, lang)

  /**
   * The [extensionProvider] function registers a concrete implementation for [FileTypeExtension]'s.
   * @see [extensionProvider] KDoc's
   */
  fun <E> IdeMetaPlugin.extensionProvider(
    FE: FileTypeExtension<E>,
    impl: E,
    fileType: LanguageFileType = KotlinFileType.INSTANCE
  ): ExtensionPhase =
    ExtensionProvider.AddFileTypeExtension(FE, impl, fileType)

  /**
   * The [extensionProvider] extension registers a concrete implementation for [ClassExtension]'s.
   * @see [extensionProvider] KDoc's
   */
  fun <E> IdeMetaPlugin.extensionProvider(
    CE: ClassExtension<E>,
    forClass: Class<*>,
    impl: E
  ): ExtensionPhase =
    ExtensionProvider.AddClassExtension(CE, forClass, impl)

  /**
   * Registers [BaseExtensionPointName]'s
   * @see [registerExtensionPoint] for [ExtensionPointName]
   */
  fun <E> IdeMetaPlugin.registerExtensionPoint(
    EP_NAME: BaseExtensionPointName<E>,
    aClass: Class<E>,
    kind: ExtensionPoint.Kind
  ): ExtensionPhase =
    ExtensionProvider.RegisterBaseExtension(EP_NAME, aClass, kind)

  /**
   * Interestingly enough, [ExtensionProvider] registers new workflows to the ide.
   * Given an example Provider:
   * ```kotlin:ank
   * import com.intellij.openapi.extensions.ExtensionPointName
   * import javax.swing.Icon
   *
   * //sampleStart
   * interface MetaProvider {
   *   fun <A> List<Icon>.helloMeta(f: (Icon) -> A): List<A>
   *   companion object {
   *     val EP_NAME: ExtensionPointName<MetaProvider> =
   *       ExtensionPointName("arrow.meta.ide.dsl.extensions.MetaProvider") // The EP_NAME is the FQ name of your Provider
   *   }
   * }
   * //sampleEnd
   * ```
   * Registering [MetaProvider] in `Meta` may look like this:
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.ide.dsl.editor.lineMarker.LineMarkerSyntax
   * import arrow.meta.invoke
   * import com.intellij.openapi.extensions.ExtensionPointName
   * import com.intellij.openapi.extensions.ExtensionPoint
   * import javax.swing.Icon
   *
   * interface MetaProvider {
   *   fun <A> List<Icon>.helloMeta(f: (Icon) -> A): List<A>
   *   companion object {
   *     val EP_NAME: ExtensionPointName<MetaProvider> =
   *       ExtensionPointName("arrow.meta.ide.dsl.extensions.MetaProvider") // The EP_NAME is the FQ name of your Provider
   *   }
   * }
   * //sampleStart
   * val IdeMetaPlugin.registeringIdeExtensions: Plugin
   *   get() = "Register ExtensionPoints" {
   *     meta(
   *        registerExtensionPoint(MetaProvider.EP_NAME, MetaProvider::class.java, ExtensionPoint.Kind.INTERFACE)
   *     )
   *   }
   * //sampleEnd
   * ```
   * @param kind There are more [resources] on the ExtensionPointKinds [here](http://www.jetbrains.org/intellij/sdk/docs/basics/plugin_structure/plugin_extension_points.html)
   */
  fun <E> IdeMetaPlugin.registerExtensionPoint(
    EP_NAME: ExtensionPointName<E>,
    aClass: Class<E>,
    kind: ExtensionPoint.Kind
  ): ExtensionPhase =
    ExtensionProvider.RegisterExtension(EP_NAME, aClass, kind)

  /**
   * registers a [ContainerProvider].
   * @param transform defines on which [PsiElement] the [ContainerProvider] is registered.
   */
  fun IdeMetaPlugin.addContainerProvider(transform: (PsiElement) -> PsiElement?): ExtensionPhase =
    extensionProvider(
      ContainerProvider.EP_NAME,
      ContainerProvider { transform(it) }
    )

  /**
   * The editor integration for [AnalysisSyntax.suppressDiagnostic].
   * @param f reuse your implementation from the compiler-plugin
   */
  fun IdeMetaPlugin.addDiagnosticSuppressor(
    f: (diagnostic: Diagnostic) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      DiagnosticSuppressor.EP_NAME,
      object : DiagnosticSuppressor {
        override fun isSuppressed(diagnostic: Diagnostic): Boolean = f(diagnostic)
      }
    )
}
