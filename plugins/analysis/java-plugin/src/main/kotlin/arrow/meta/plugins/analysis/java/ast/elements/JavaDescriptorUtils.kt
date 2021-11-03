@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.elements

import javax.lang.model.element.Element
import javax.lang.model.element.QualifiedNameable

public val Element.fqName: String
  get() =
    when (this) {
      is QualifiedNameable -> qualifiedName.toString()
      else -> enclosingElement.fqName + "." + simpleName.toString()
    }
