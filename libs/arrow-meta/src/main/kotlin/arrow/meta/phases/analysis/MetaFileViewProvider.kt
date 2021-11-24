package arrow.meta.phases.analysis

import org.jetbrains.kotlin.com.intellij.openapi.editor.Document
import org.jetbrains.kotlin.com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.SingleRootFileViewProvider

/**
 * Provides interception access to the internals of a [VirtualFile] allowing to replace its
 * [Document]
 */
class MetaFileViewProvider(psiManager: PsiManager, virtualFile: VirtualFile, val newText: String) :
  SingleRootFileViewProvider(psiManager, virtualFile) {
  override fun getDocument(): Document? = super.getDocument()?.also { it.setText(newText) }
  override fun getContents(): CharSequence = newText
}
