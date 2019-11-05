package arrow.meta.ide.dsl.extensions

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.phases.editor.AddClassExtension
import arrow.meta.ide.phases.editor.AddExtension
import arrow.meta.ide.phases.editor.AddFileTypeExtension
import arrow.meta.ide.phases.editor.AddLanguageExtension
import arrow.meta.ide.phases.editor.RegisterBaseExtension
import arrow.meta.ide.phases.editor.RegisterExtension
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.ContainerProvider
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.fileTypes.FileTypeExtension
import com.intellij.openapi.util.ClassExtension
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.resolve.diagnostics.DiagnosticSuppressor


interface ExtensionProviderSyntax {
  // Todo: Check LoadingOrder
  fun <E> IdeMetaPlugin.extensionProvider(
    EP_NAME: ExtensionPointName<E>,
    impl: E,
    loadingOrder: LoadingOrder = LoadingOrder.ANY
  ): ExtensionPhase =
    AddExtension(EP_NAME, impl, loadingOrder)

  fun <E> IdeMetaPlugin.extensionProvider(
    LE: LanguageExtension<E>,
    impl: E
  ): ExtensionPhase =
    AddLanguageExtension(LE, impl)

  fun <E> IdeMetaPlugin.extensionProvider(
    FE: FileTypeExtension<E>,
    impl: E
  ): ExtensionPhase =
    AddFileTypeExtension(FE, impl)

  fun <E> IdeMetaPlugin.extensionProvider(
    CE: ClassExtension<E>,
    forClass: Class<*>,
    impl: E
  ): ExtensionPhase =
    AddClassExtension(CE, forClass, impl)

  fun <E> IdeMetaPlugin.registerExtensionPoint(
    EP_NAME: BaseExtensionPointName,
    aClass: Class<E>
  ): ExtensionPhase =
    RegisterBaseExtension(EP_NAME, aClass)

  fun <E> IdeMetaPlugin.registerExtensionPoint(
    EP_NAME: ExtensionPointName<E>,
    aClass: Class<E>
  ): ExtensionPhase =
    RegisterExtension(EP_NAME, aClass)

  fun IdeMetaPlugin.addContainerProvider(f: (PsiElement) -> PsiElement?): ExtensionPhase =
    extensionProvider(
      ContainerProvider.EP_NAME,
      ContainerProvider { f(it) }
    )

  /**
   * Check out [org.jetbrains.kotlin.resolve.checkers.PlatformDiagnosticSuppressor] for further improvements
   */
  fun IdeMetaPlugin.addDiagnosticSuppressor(
    isSuppressed: (diagnostic: Diagnostic) -> Boolean
  ): ExtensionPhase =
    extensionProvider(
      DiagnosticSuppressor.EP_NAME,
      object : DiagnosticSuppressor {
        override fun isSuppressed(diagnostic: Diagnostic): Boolean =
          isSuppressed(diagnostic)
      }
    )
}
