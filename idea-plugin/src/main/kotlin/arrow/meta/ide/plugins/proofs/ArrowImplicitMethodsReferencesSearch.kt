package arrow.meta.ide.plugins.proofs

import arrow.meta.ide.dsl.application.ApplicationSyntax
import arrow.meta.ide.plugins.proofs.markers.participatingTypes
import arrow.meta.plugins.proofs.phases.coerceProof
import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.PsiReference
import com.intellij.psi.search.searches.MethodReferencesSearch
import com.intellij.util.IncorrectOperationException
import com.intellij.util.Processor
import org.jetbrains.kotlin.asJava.elements.KtLightMethod
import org.jetbrains.kotlin.idea.caches.resolve.util.getJavaMethodDescriptor
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Adds implicit calls of methods to the results of Find Usages.
 */
class ArrowImplicitMethodsReferencesSearch : QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters>(true), ApplicationSyntax {
  override fun processQuery(queryParameters: MethodReferencesSearch.SearchParameters, consumer: Processor<in PsiReference>) {
    val method = queryParameters.method
    if (method !is KtLightMethod) {
      return
    }

    // fixme handle dumb mode
    // fixme use PsiManager batch processing?

    // fixme currently this is a proof of concept with the currently opened files only
    val currentFiles = FileEditorManager.getInstance(method.project).selectedFiles.mapNotNull {
      PsiManager.getInstance(method.project).findFile(it)
    }
    if (currentFiles.isEmpty()) {
      return
    }

    val visitor = object : PsiRecursiveElementVisitor() {
      override fun visitElement(element: PsiElement) {
        if (element is KtProperty) {
          element.participatingTypes()?.let { (subtype, supertype) ->
            val through = element.ctx()?.coerceProof(subtype, supertype)?.through
            if (through == null || through.name.asString() != method.name) {
              return
            }

            val rhs = element.initializer ?: return
            consumer.process(StaticPsiReference(rhs))
          }
        } else {
          // visit remaining elements
          super.visitElement(element)
        }
      }
    }

    currentFiles.forEach(visitor::visitElement)
  }
}

internal class StaticPsiReference(private val element: PsiElement) : PsiReference {
  override fun getElement(): PsiElement = element

  override fun getRangeInElement(): TextRange = TextRange.create(0, element.textLength)

  override fun bindToElement(element: PsiElement): PsiElement {
    throw IncorrectOperationException("unsupported")
  }

  override fun isReferenceTo(target: PsiElement): Boolean {
    return element == target
  }

  override fun resolve(): PsiElement? {
    return element
  }

  override fun getCanonicalText(): String {
    return element.text
  }

  override fun handleElementRename(newElementName: String): PsiElement {
    throw IncorrectOperationException("unsupported")
  }

  override fun isSoft(): Boolean = true
}
