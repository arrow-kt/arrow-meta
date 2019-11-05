package arrow.meta.ide.phases.editor

import arrow.meta.phases.ExtensionPhase
import com.intellij.core.JavaCoreApplicationEnvironment
import com.intellij.lang.LanguageExtension
import com.intellij.openapi.extensions.BaseExtensionPointName
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.LoadingOrder
import com.intellij.openapi.fileTypes.FileTypeExtension
import com.intellij.openapi.util.ClassExtension

/**
 * We're lifting the arguments of the interpreter to data types in an ADT and as we extend our Algebra of manipulating the
 * Editor environment, so does the ADT. Additionally, we're promoting users to compute only one value at a time and not a list of manipulations, which we cant control
 * as we do in the InternalRegistry in the compiler. The ADT gives as more granularity, because we're essentially asking for a SumType of a specific shape.
 * Now we're also getting the RunTimeExceptions, due to MemoryLeaks we discuss in ...
 */
sealed class ExtensionProvider<E> : ExtensionPhase

data class AddExtension<E>(val EP_NAME: ExtensionPointName<E>, val impl: E, val loadingOrder: LoadingOrder) : ExtensionProvider<E>()
data class AddLanguageExtension<E>(val LE: LanguageExtension<E>, val impl: E) : ExtensionProvider<E>()

/**
 * Examples are here: [JavaCoreApplicationEnvironment] line 57, 58
 */
data class AddFileTypeExtension<E>(val FE: FileTypeExtension<E>, val impl: E) : ExtensionProvider<E>()

/**
 * Examples are here: [JavaCoreApplicationEnvironment] line 72 - 77
 * kotlinx.metadata.jvm.impl.JvmClassExtension is internal
 */
data class AddClassExtension<E>(val CE: ClassExtension<E>, val forClass: Class<*>, val impl: E) : ExtensionProvider<E>()

data class RegisterBaseExtension<E>(val EP_NAME: BaseExtensionPointName, val aClass: Class<E>) : ExtensionProvider<E>()
data class RegisterExtension<E>(val EP_NAME: ExtensionPointName<E>, val aClass: Class<E>) : ExtensionProvider<E>()
