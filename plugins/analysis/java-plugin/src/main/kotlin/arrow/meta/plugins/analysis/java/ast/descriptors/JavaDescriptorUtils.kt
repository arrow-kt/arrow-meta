@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.descriptors

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.QualifiedNameable
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.element.VariableElement

public val Element.fqName: String
  get() =
    when (this) {
      is QualifiedNameable -> qualifiedName.toString()
      else -> enclosingElement.fqName + "." + simpleName.toString()
    }

public val Element.enclosingClass: TypeElement?
  get() =
    when (val encl = this.enclosingElement) {
      null -> null
      is TypeElement -> encl
      else -> encl.enclosingClass
    }

public val Element.typeParametersFromEverywhere: List<TypeParameterElement>
  get() =
    when (this) {
      is ExecutableElement -> typeParameters
      is TypeElement -> typeParameters
      else -> emptyList()
    }

public val Element.parametersFromEverywhere: List<VariableElement>
  get() =
    when (this) {
      is ExecutableElement -> parameters
      else -> emptyList()
    }
