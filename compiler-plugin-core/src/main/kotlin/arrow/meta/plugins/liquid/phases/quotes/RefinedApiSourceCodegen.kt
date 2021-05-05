package arrow.meta.plugins.liquid.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.body
import arrow.meta.phases.findInAnalysedDescriptors
import arrow.meta.quotes.Transform
import arrow.meta.quotes.TypedQuoteTemplate
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.objectDeclaration
import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.resolve.calls.inference.returnTypeOrNothing
import org.jetbrains.kotlin.resolve.descriptorUtil.isSubclassOf
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.resolve.descriptorUtil.parentsWithSelf

internal fun CompilerContext.generateRefinedApi(meta: Meta): ExtensionPhase =
  meta.objectDeclaration(this@generateRefinedApi, ::isRefined) {
    val ktLambda = refinedLambda()
    val refinedLambdaDescriptor = ktLambda?.findInAnalysedDescriptors<AnonymousFunctionDescriptor>(this@generateRefinedApi)
    if (refinedLambdaDescriptor != null) {
      val file = generateRefinedApi(this, ktLambda, refinedLambdaDescriptor)
      Transform.newSources(file)
    } else Transform.empty
  }

fun isRefined(template: TypedQuoteTemplate<KtObjectDeclaration, ClassDescriptor>): Boolean {
  val refinedClassId = ClassId.fromString("arrow/refinement/Refined")
  val refinedClass = template.descriptor?.module?.findClassAcrossModuleDependencies(refinedClassId)
  return refinedClass != null && template.descriptor.isSubclassOf(refinedClass)
}

private fun ObjectDeclaration.refinedLambda(): KtFunctionLiteral? =
  value.superTypeListEntries
    .filterIsInstance<KtSuperTypeCallEntry>()
    .flatMap { it.valueArguments }
    .map { it.getArgumentExpression() }
    .filterIsInstance<KtLambdaExpression>()
    .map { it.functionLiteral }.firstOrNull()

private fun CompilerContext.generateRefinedApi(
  companion: ObjectDeclaration,
  ktFunction: KtFunctionLiteral,
  fDescriptor: AnonymousFunctionDescriptor
): File =
  companion.run {
    val refinedTypeName = descriptor?.parents?.firstOrNull()?.name
    val params = fDescriptor.valueParameters.joinToString { "${it.name}: ${it.type}" }
    val args = fDescriptor.valueParameters.joinToString { "${it.name}" }
    """
    package ${descriptor?.findPackage()?.fqName?.asString()}
    
    import arrow.refinement.*
    
    fun require${refinedTypeName}($params): Unit =
      require(${ktFunction.body()?.text?.replace("ensure(", "ensureA(")})
    
    """.trimIndent().file("$refinedTypeName.refined")
  }
