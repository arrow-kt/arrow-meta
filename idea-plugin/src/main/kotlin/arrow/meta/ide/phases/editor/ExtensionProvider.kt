package arrow.meta.ide.phases.editor

import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.core.CoreApplicationEnvironment
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.fileTypes.FileTypeExtension
import com.intellij.openapi.util.ClassExtension
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.KotlinLanguage

/**
 *
 * 3 ExtensionPhases ->
 */


interface ExtensionProvider2 : ExtensionPhase {
  fun <E> addExtension(EP_NAME: ExtensionPointName<E>, impl: E, loadingOrder: LoadingOrder): Unit
  fun <E> addLanguageExtension(LE: LanguageExtension<E>, impl: E): Unit
  fun <E> addFileTypeExtension(FE: FileTypeExtension<E>, impl: E): Unit
  fun <E> addClassExtension(CE: ClassExtension<E>, forClass: Class<*>, impl: E): Unit
  fun <E> registerExtension(EP_NAME: BaseExtensionPointName, aClass: Class<E>): Unit
  fun <E> registerExtension(EP_NAME: ExtensionPointName<E>, aClass: Class<E>): Unit
}


fun <E> extensionProvider2( // in ExtensionProviderSyntax
  addExtension: (EP_NAME: ExtensionPointName<E>, impl: E, loadingOrder: LoadingOrder) -> Unit = Noop.effect3,
  addLanguageExtension: (LE: LanguageExtension<E>, impl: E) -> Unit = Noop.effect2,
  addFileTypeExtension: (FE: FileTypeExtension<E>, impl: E) -> Unit = Noop.effect2,
  addClassExtension: (CE: ClassExtension<E>, forClass: Class<*>, impl: E) -> Unit = Noop.effect3,
  registerBaseExtension: (EP_NAME: BaseExtensionPointName, aClass: Class<E>) -> Unit = Noop.effect2,
  registerExtension: (EP_NAME: ExtensionPointName<E>, aClass: Class<E>) -> Unit = Noop.effect2
): ExtensionPhase =
  object : ExtensionProvider2 {
    override fun <E> addExtension(EP_NAME: ExtensionPointName<E>, impl: E, loadingOrder: LoadingOrder) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <E> addLanguageExtension(LE: LanguageExtension<E>, impl: E) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <E> addFileTypeExtension(FE: FileTypeExtension<E>, impl: E) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <E> addClassExtension(CE: ClassExtension<E>, forClass: Class<*>, impl: E) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <E> registerExtension(EP_NAME: BaseExtensionPointName, aClass: Class<E>) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <E> registerExtension(EP_NAME: ExtensionPointName<E>, aClass: Class<E>) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
  }

interface ExtensionProvider {
  val dispose: Disposable
    get() = Disposer.newDisposable()

  fun <E> addExtension(EP_NAME: ExtensionPointName<E>, impl: E, loadingOrder: LoadingOrder): Unit =
    Extensions.getRootArea().getExtensionPoint(EP_NAME).registerExtension(impl, loadingOrder, dispose)
  // lift this function as an interpreter

  fun <E> addLanguageExtension(LE: LanguageExtension<E>, impl: E): Unit =
    LE.addExplicitExtension(KotlinLanguage.INSTANCE, impl)

  /**
   * Examples are here: [JavaCoreApplicationEnvironment] line 57, 58
   */
  fun <E> addFileTypeExtension(FE: FileTypeExtension<E>, impl: E): Unit =
    FE.addExplicitExtension(KotlinFileType.INSTANCE, impl)

  /**
   * Examples are here: [JavaCoreApplicationEnvironment] line 72 - 77
   * kotlinx.metadata.jvm.impl.JvmClassExtension is internal
   */
  fun <E> addClassExtension(CE: ClassExtension<E>, forClass: Class<*>, impl: E): Unit =
    CE.addExplicitExtension(forClass, impl)

  fun <E> registerExtension(EP_NAME: BaseExtensionPointName, aClass: Class<E>): Unit =
    CoreApplicationEnvironment.registerExtensionPoint(Extensions.getRootArea(), EP_NAME, aClass)

  fun <E> registerExtension(EP_NAME: ExtensionPointName<E>, aClass: Class<E>): Unit =
    CoreApplicationEnvironment.registerExtensionPoint(Extensions.getRootArea(), EP_NAME, aClass)
}
