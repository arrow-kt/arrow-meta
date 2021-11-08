@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.types

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.TypeProjection
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable

public class JavaType(private val ctx: AnalysisContext, internal val ty: TypeMirror) : Type {
  override val descriptor: ClassDescriptor?
    get() =
      ty.visit(
        object : OurTypeVisitor<ClassDescriptor?>(null) {
          override fun visitDeclared(t: DeclaredType?, p: TypeMirror?): ClassDescriptor? =
            t?.asElement()?.model(ctx)
        }
      )

  override fun isNullable(): Boolean = false
  override val unwrappedNotNullableType: Type = this
  override val isMarkedNullable: Boolean = false

  override val arguments: List<TypeProjection>
    get() =
      ty.visit(
        object : OurTypeVisitor<List<TypeProjection>>(emptyList()) {
          override fun visitArray(t: ArrayType?, p: TypeMirror?): List<TypeProjection> =
            listOfNotNull(t?.componentType).map { JavaTypeProjection(ctx, it) }
          override fun visitDeclared(t: DeclaredType?, p: TypeMirror?): List<TypeProjection> =
            t?.typeArguments?.map { JavaTypeProjection(ctx, it) }.orEmpty()
        }
      )

  private fun isEqualTo(other: TypeMirror): Boolean = ctx.types.isSameType(ty, other)

  override fun isBoolean(): Boolean = isEqualTo(ctx.symbolTable.booleanType)
  override fun isInt(): Boolean = isEqualTo(ctx.symbolTable.intType)
  override fun isLong(): Boolean = isEqualTo(ctx.symbolTable.longType)
  override fun isFloat(): Boolean = isEqualTo(ctx.symbolTable.floatType)
  override fun isDouble(): Boolean = isEqualTo(ctx.symbolTable.doubleType)
  override fun isByte(): Boolean = isEqualTo(ctx.symbolTable.byteType)
  override fun isShort(): Boolean = isEqualTo(ctx.symbolTable.shortType)
  override fun isUnsignedNumberType(): Boolean = false // TODO
  override fun isChar(): Boolean = isEqualTo(ctx.symbolTable.charType)
  override fun isAnyOrNullableAny(): Boolean = isEqualTo(ctx.symbolTable.objectType)

  override fun isSubtypeOf(other: Type): Boolean {
    if (other !is JavaType) return false
    return ctx.types.isSubtype(ty, other.ty)
  }
  override fun isEqualTo(other: Type): Boolean {
    if (other !is JavaType) return false
    return ctx.types.isSameType(ty, other.ty)
  }

  override fun isTypeParameter(): Boolean =
    ty.visit(
      object : OurTypeVisitor<Boolean>(false) {
        override fun visitTypeVariable(t: TypeVariable?, p: TypeMirror?): Boolean = true
      }
    )
}
