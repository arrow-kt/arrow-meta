package arrow.meta.plugins.liquid.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.analysis.body
import arrow.meta.quotes.Scope
import arrow.meta.quotes.Transform
import arrow.meta.quotes.TypedQuoteTemplate
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.objectDeclaration
import org.jetbrains.kotlin.backend.common.serialization.findPackage
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.isSubclassOf
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.descriptorUtil.parents
import org.jetbrains.kotlin.types.KotlinType

internal fun CompilerContext.generateRefinedApi(meta: Meta): ExtensionPhase =
  meta.objectDeclaration(this@generateRefinedApi, ::isRefined) {
    val refExp = refinedExpressions(this)
    val file = generateRefinedApi(this, refExp)
    Transform.newSources(file)
  }

private fun isRefined(template: TypedQuoteTemplate<KtObjectDeclaration, ClassDescriptor>): Boolean {
  val refinedClassId = ClassId.fromString("arrow/refinement/Refined")
  val refinedClass = template.descriptor?.module?.findClassAcrossModuleDependencies(refinedClassId)
  return refinedClass != null && template.descriptor!!.isSubclassOf(refinedClass)
}

private fun CompilerContext.refinedExpressions(declaration: ObjectDeclaration): List<KtExpression> =
  declaration.supertypesArgumentAndExpressions(this)
    .flatMap { (_, exp, _) ->
      when (exp) {
        /* a local simple construction */
        is KtLambdaExpression -> listOfNotNull(exp.functionLiteral.body())
        is KtNameReferenceExpression -> {
          val localDescriptor = bindingTrace?.getType(exp)?.constructor?.declarationDescriptor as? ClassDescriptor
          val localPsi = localDescriptor?.findPsi()
          if (localDescriptor != null && localPsi != null && localPsi is KtObjectDeclaration) {
            refinedExpressions(ObjectDeclaration(localPsi, localDescriptor))
          } else {
            val fqName = bindingTrace?.bindingContext?.getType(exp)?.constructor?.declarationDescriptor?.fqNameSafe
            listOf("constraints($fqName, it)".expression.value)
          }
        }
        else -> emptyList<KtExpression>()
      }
    }.filterNotNull()

private fun ObjectDeclaration.supertypesArgumentAndExpressions(compilerContext: CompilerContext): List<Triple<KtValueArgument, KtExpression, KotlinType?>> =
  supertypesArguments(compilerContext).filterIsInstance<KtValueArgument>().mapNotNull {
    it.getArgumentExpression()?.let { expr ->
      Triple(it, expr, compilerContext.bindingTrace?.bindingContext?.getType(expr))
    }
  }

private fun ObjectDeclaration.supertypesArgumentExpressions(compilerContext: CompilerContext): List<KtExpression> =
  supertypesArguments(compilerContext).mapNotNull { it.getArgumentExpression() }

private fun ObjectDeclaration.supertypesArguments(compilerContext: CompilerContext): List<ValueArgument> =
  compilerContext.run {
    value.superTypeListEntries
      .filterIsInstance<KtSuperTypeCallEntry>()
      .flatMap {
        it.valueArguments
      }
  }

private fun CompilerContext.generateRefinedApi(
  companion: ObjectDeclaration,
  refExpression: List<KtExpression>
): File =
  companion.run {
    val parentDescriptor = descriptor?.parents?.firstOrNull()
    val refinedTypeName = parentDescriptor?.name
    val refinedTarget = parentDescriptor.refinementTarget()
    val paramsL = superTypeExpressionVaueArguments(companion, refinedTarget)
    val params = if (paramsL.isEmpty()) "it: $refinedTarget" else paramsL.joinToString()
    val predicates = refExpression.toCompileTimePredicates()
    val mappedExpr = replaceEnsureCalls(predicates)
    refineCodegenTemplate(this, refinedTypeName, params, mappedExpr)
  }

private fun ElementScope.refineCodegenTemplate(
  objectDeclaration: ObjectDeclaration,
  refinedTypeName: Name?,
  params: String,
  mappedExpr: Scope<KtExpression>
): File = """
    package ${objectDeclaration.descriptor?.findPackage()?.fqName?.asString()}
    
    import arrow.refinement.*
    
    fun require$refinedTypeName($params): Unit =
      require($mappedExpr)
    
    """.trimIndent().file("$refinedTypeName.refined")

private fun CompilerContext.superTypeExpressionVaueArguments(
  companion: ObjectDeclaration,
  refinedTarget: KotlinType?
): List<String> =
  companion.supertypesArgumentExpressions(this).filterIsInstance<KtLambdaExpression>().map {
    if (it.valueParameters.isEmpty()) "it: $refinedTarget"
    else it.functionLiteral.valueParameters.joinToString { "${it.name}: $refinedTarget" }
  }

private fun DeclarationDescriptor?.refinementTarget(): KotlinType? =
  (this as? ClassDescriptor)?.constructors?.firstOrNull()?.valueParameters?.firstOrNull()?.returnType

private fun ElementScope.replaceEnsureCalls(predicates: List<String>): Scope<KtExpression> =
  predicates.joinToString(prefix = "ensureA(", postfix = ")") {
    if (it.startsWith("ensure(")) it.replace("ensure", "ensureA")
    else it
  }.expression

private fun List<KtExpression>.toCompileTimePredicates() =
  map {
    if (it.text.startsWith("ensure")) it.text.replaceFirst("ensure", "ensureA")
    else it.text
  }
