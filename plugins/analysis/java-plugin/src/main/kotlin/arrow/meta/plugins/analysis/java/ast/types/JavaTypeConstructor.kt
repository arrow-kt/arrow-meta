@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java.ast.types

import arrow.meta.plugins.analysis.java.AnalysisContext
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ClassifierDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeConstructor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.TypeParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeKind

public class JavaTypeConstructor(private val ctx: AnalysisContext, private val impl: TypeElement) :
  TypeConstructor {
  override val parameters: List<TypeParameterDescriptor>
    get() = impl.typeParameters.map { it.model(ctx) }
  override val supertypes: Collection<Type>
    get() =
      (listOfNotNull(impl.superclass.takeIf { it.kind != TypeKind.NONE }) + impl.interfaces).map {
        it.model(ctx)
      }
  override val isFinal: Boolean
    get() = impl.modifiers.contains(Modifier.FINAL)
  override val isDenotable: Boolean = true
  override val declarationDescriptor: ClassifierDescriptor
    get() = impl.model(ctx)
}
