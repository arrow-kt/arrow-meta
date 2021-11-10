@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.types

import arrow.meta.plugins.analysis.java.AnalysisContext
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.SimpleTypeVisitor9

public typealias OurTypeVisitor<R> = SimpleTypeVisitor9<R, Unit>

public fun <R> TypeMirror.visit(visitor: OurTypeVisitor<R>): R = this.accept(visitor, Unit)

public fun TypeMirror.allSupertypes(ctx: AnalysisContext): Set<TypeMirror> =
  ctx.types.directSupertypes(this).flatMap { setOf(it) + it.allSupertypes(ctx) }.toSet()
