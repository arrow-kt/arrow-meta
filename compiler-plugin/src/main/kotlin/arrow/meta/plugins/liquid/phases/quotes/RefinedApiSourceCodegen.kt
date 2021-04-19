package arrow.meta.plugins.liquid.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.analysis.dfs
import arrow.meta.plugins.liquid.phases.ir.refinedAnnotationFqName
import arrow.meta.quotes.Transform
import arrow.meta.quotes.TypedQuoteTemplate
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.namedFunction
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.resolve.descriptorUtil.module

internal fun CompilerContext.generateRefinedApi(meta: Meta): ExtensionPhase =
  meta.namedFunction(this@generateRefinedApi, { isRefinedCompileFunction() }) {
    val predicates = constraintCallExpressions().predicatesForRefinedDeclaration()
    val arg = refinedArgumentName()
    val validatedSupport = validatedClassDescriptor()
    val file = generateRefinedApi(this, validatedSupport, predicates, arg)
    Transform.newSources(file)
  }

private fun ElementScope.generateRefinedApi(
  namedFunction: NamedFunction,
  validatedSupport: ClassDescriptor?,
  predicates: Map<String, String>,
  arg: String?
): File =
  namedFunction.run {
    """
    package ${descriptor?.findPackage()?.fqName?.asString()}
          
    ${validatedSupport.validatedImports()}
    ${refinedTypeAlias()}     
    ${generateNullableApi(predicates, arg)}
    ${generateValidatedApi(validatedSupport, arg, predicates)}
    """.trimIndent().file("$name.refined")
  }



private fun TypedQuoteTemplate<KtNamedFunction, FunctionDescriptor>.isRefinedCompileFunction(): Boolean =
  descriptor?.annotations?.any { it.fqName == refinedAnnotationFqName } == true

private fun NamedFunction.constraintCallExpressions(): List<KtCallExpression> =
  value.dfs {
    it is KtCallExpression && it.calleeExpression?.text == "constraint"
  }.filterIsInstance<KtCallExpression>()

private fun NamedFunction.generateValidatedApi(
  validatedSupport: ClassDescriptor?,
  arg: String?,
  predicates: Map<String, String>
) = if (validatedSupport != null) {
  """
  inline fun $`(typeParameters)` ${name}Validated$`(params)` : ValidatedNel<String, ${returnType.value.first().text}> = 
    $arg.validate(mapOf(${predicates.entries.joinToString { (key, value) -> "\"$key\" to ($value)" }})) 
  """.trimIndent()
} else ""

private fun NamedFunction.generateNullableApi(
  predicates: Map<String, String>,
  arg: String?
): String =
  """inline fun $`(typeParameters)` ${name}OrNull$`(params)`$returnType? = 
    |  if (${predicates.predicatesToIfConstrain()}) $arg else null""".trimMargin()

private fun NamedFunction.refinedTypeAlias(): String =
  """typealias $name${`(typeParameters)`} = ${descriptor?.valueParameters?.first()?.type.toString()}"""

private fun ClassDescriptor?.validatedImports(): String =
  if (this != null)
    """
      import arrow.core.*
       
      //TODO generate once
      @PublishedApi
      internal fun <A> A.validate(constrains: Map<String, Boolean>): ValidatedNel<String, A> =
        if (constrains.entries.isNotEmpty()) {
          constrains.entries.map { (msg, passed) ->
            if (passed) validNel()
            else msg.invalidNel()
          }.combineAll(Monoid.validated(Semigroup.nonEmptyList(), IdentityMonoid(this)))
        } else "empty constrains for ${'$'}this".invalidNel()
    """.trimIndent() else ""

private fun NamedFunction.validatedClassDescriptor(): ClassDescriptor? =
  descriptor?.module?.findClassAcrossModuleDependencies(ClassId.fromString("arrow/core/Validated"))

private fun Map<String, String>.predicatesToIfConstrain(): String =
  entries.joinToString(" && ") { it.value }

private fun NamedFunction.refinedArgumentName(): String? =
  `(params)`.value.first().name

private fun List<KtCallExpression>.predicatesForRefinedDeclaration(): Map<String, String> =
  associate {
    it.valueArguments[1].text to it.valueArguments[0].text
  }



