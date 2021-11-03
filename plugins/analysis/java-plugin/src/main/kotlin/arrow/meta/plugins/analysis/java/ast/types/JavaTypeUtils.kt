@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.types

import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleTypeVisitor9

public typealias OurTypeVisitor<R> = SimpleTypeVisitor9<R, TypeMirror>

public fun <R> TypeMirror.visit(visitor: OurTypeVisitor<R>): R = this.accept(visitor, this)
