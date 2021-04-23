package arrow.meta.plugins.liquid.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.TypedQuoteTemplate
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.Property
import arrow.meta.quotes.property
import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf

internal fun CompilerContext.generateRefinedApi(meta: Meta): ExtensionPhase =
  meta.property(this@generateRefinedApi, { isRefinedCompileFunction() }) {
    val file = generateRefinedApi(this, mapOf())
    Transform.newSources(file)
  }

fun TypedQuoteTemplate<KtProperty, PropertyDescriptor>.isRefinedCompileFunction(): Boolean {
  val predicateDescriptor = descriptor?.module?.findClassAcrossModuleDependencies(ClassId.fromString("arrow/Predicate"))
  return if (predicateDescriptor != null) {
    descriptor?.type?.constructor == predicateDescriptor.typeConstructor
  } else false
}

private fun ElementScope.generateRefinedApi(
  property: Property,
  predicates: Map<String, String>
): File =
  property.run {
    """
    package ${descriptor?.findPackage()?.fqName?.asString()}
    
    import arrow.Predicate
    """.trimIndent().file("$name.refined")
  }
