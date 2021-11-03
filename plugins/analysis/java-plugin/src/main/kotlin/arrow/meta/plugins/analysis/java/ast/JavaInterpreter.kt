@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.elements.JavaDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaModuleDescriptor
import javax.lang.model.element.Element
import javax.lang.model.element.ModuleElement

public fun <A : Element, B : JavaDescriptor> A.model(ctx: AnalysisContext): B =
  when (this) {
    is ModuleElement -> JavaModuleDescriptor(ctx, this) as B
    else -> JavaDescriptor(ctx, this) as B
  }
