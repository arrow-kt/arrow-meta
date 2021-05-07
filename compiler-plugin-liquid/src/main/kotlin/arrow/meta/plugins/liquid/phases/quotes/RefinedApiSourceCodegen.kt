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
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFunctionLiteral
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSuperTypeCallEntry
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.ValueArgument
import org.jetbrains.kotlin.resolve.bindingContextUtil.getReferenceTargets
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

fun isRefined(template: TypedQuoteTemplate<KtObjectDeclaration, ClassDescriptor>): Boolean {
  val refinedClassId = ClassId.fromString("arrow/refinement/Refined")
  val refinedClass = template.descriptor?.module?.findClassAcrossModuleDependencies(refinedClassId)
  return refinedClass != null && template.descriptor!!.isSubclassOf(refinedClass)
}

private fun CompilerContext.refinedExpressions(declaration: ObjectDeclaration): List<KtExpression> =
  declaration.supertypesArgumentAndExpressions(this)
    .flatMap { (arg, exp, type) ->
      when (exp) {
        /* a local simple construction */
        is KtLambdaExpression -> listOfNotNull(exp.functionLiteral.body())
        is KtNameReferenceExpression -> {
          val localDescriptor = bindingTrace?.getType(exp)?.constructor?.declarationDescriptor as? ClassDescriptor
          val localPsi = localDescriptor?.findPsi()
          if (localDescriptor != null && localPsi != null && localPsi is KtObjectDeclaration) {
            refinedExpressions(ObjectDeclaration(localPsi, localDescriptor))
          } else {
            listOf("${exp.text}.constrains(it)".expression.value)
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
    val refinedTarget =
      (parentDescriptor as? ClassDescriptor)?.constructors?.firstOrNull()?.valueParameters?.firstOrNull()?.returnType
    val paramsL =
      companion.supertypesArgumentExpressions(this@generateRefinedApi).filterIsInstance<KtLambdaExpression>().map {
        if (it.valueParameters.isEmpty()) "it: $refinedTarget"
        else it.functionLiteral.valueParameters.joinToString { "${it.name}: $refinedTarget" }
      }
    val params = if (paramsL.isEmpty()) "it: $refinedTarget" else paramsL.joinToString()
    val predicates = refExpression.flatMap { it.children.toList() }.filterIsInstance<KtCallExpression>().flatMap { it.valueArguments }.map { it.text }
    val mappedExpr = predicates.joinToString(prefix = "ensureA(", postfix = ")").expression
    val args = companion.supertypesArgumentExpressions(this@generateRefinedApi).filterIsInstance<KtLambdaExpression>()
      .joinToString {
        if (it.valueParameters.isEmpty()) "it"
        else it.functionLiteral.valueParameters.joinToString { "${it.name}" }
      }
    """
    package ${descriptor?.findPackage()?.fqName?.asString()}
    
    import arrow.refinement.*
    
    fun require${refinedTypeName}($params): Unit =
      require(${mappedExpr})
    
    """.trimIndent().file("$refinedTypeName.refined")
  }
