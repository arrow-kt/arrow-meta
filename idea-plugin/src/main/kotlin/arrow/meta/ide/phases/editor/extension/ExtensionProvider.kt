package arrow.meta.ide.phases.editor.extension

import arrow.meta.ide.dsl.extensions.ExtensionProviderSyntax
import arrow.meta.phases.ExtensionPhase
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.lang.annotation.Annotator
import arrow.meta.ide.dsl.editor.annotator.AnnotatorSyntax
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPoint
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.fileTypes.FileTypeExtension
import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.ClassExtension

/**
 * @see [ExtensionProviderSyntax]
 */
sealed class ExtensionProvider<E> : ExtensionPhase {
  /**
   * @see [ExtensionProviderSyntax.extensionProvider]
   */
  data class AddExtension<E>(val EP_NAME: ExtensionPointName<E>, val impl: E, val loadingOrder: LoadingOrder) : ExtensionProvider<E>()

  /**
   * @see [ExtensionProviderSyntax.extensionProvider]
   */
  data class AddLanguageExtension<E>(val LE: LanguageExtension<E>, val impl: E, val lang: Language) : ExtensionProvider<E>()

  /**
   * @see AnnotatorSyntax
   */
  data class AddLanguageAnnotator(val lang: Language, val impl: Annotator) : ExtensionProvider<Annotator>()

  /**
   * Examples are here: [JavaCoreApplicationEnvironment] line 57, 58
   * @see [ExtensionProviderSyntax.extensionProvider]
   */
  data class AddFileTypeExtension<E>(val FE: FileTypeExtension<E>, val impl: E, val fileType: LanguageFileType) : ExtensionProvider<E>()

  /**
   * Examples are here: [JavaCoreApplicationEnvironment] line 72 - 77
   * kotlinx.metadata.jvm.impl.JvmClassExtension is internal
   * @see [ExtensionProviderSyntax.extensionProvider]
   */
  data class AddClassExtension<E>(val CE: ClassExtension<E>, val forClass: Class<*>, val impl: E) : ExtensionProvider<E>()

  /**
   * @see [ExtensionProviderSyntax.registerExtensionPoint]
   */
  data class RegisterBaseExtension<E>(val EP_NAME: BaseExtensionPointName<E>, val aClass: Class<E>, val kind: ExtensionPoint.Kind) : ExtensionProvider<E>()

  /**
   * @see [ExtensionProviderSyntax.registerExtensionPoint]
   */
  data class RegisterExtension<E>(val EP_NAME: ExtensionPointName<E>, val aClass: Class<E>, val kind: ExtensionPoint.Kind) : ExtensionProvider<E>()
}