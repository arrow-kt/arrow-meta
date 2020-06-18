package arrow.meta.plugins.proofs.phases.quotes

import arrow.meta.Meta
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.analysis.traverseFilter
import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.file
import arrow.meta.quotes.filebase.File
import arrow.meta.quotes.foldIndexed
import arrow.meta.quotes.map
import arrow.meta.quotes.modifierlistowner.TypeReference
import arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner.NamedFunction
import arrow.meta.quotes.plus
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter

fun CompilerContext.generateGivenExtensionsFile(meta: Meta): ExtensionPhase =
  meta.file(this, KtFile::containsGivenConstrains) {
    Transform.newSources(
      """
      $importList
      ${generateGivenSupportingFunctions(givenConstrainedDeclarations())}
      """.file("Extensions.$name")
    )
  }

private val givenAnnotation: Regex = Regex("@(arrow\\.)?Given")

private fun KtFile.containsGivenConstrains(): Boolean =
  givenConstrainedDeclarations().isNotEmpty()

private fun File.givenConstrainedDeclarations(): List<NamedFunction> =
  value.givenConstrainedDeclarations().map { NamedFunction(it, null) }

private fun KtFile.givenConstrainedDeclarations(): List<KtNamedFunction> =
  traverseFilter(KtNamedFunction::class.java) {
    it.takeIf { f -> f.containsGivenConstrain() }
  }

private fun ElementScope.generateGivenSupportingFunctions(functions: List<NamedFunction>): ScopedList<KtNamedFunction> =
  ScopedList(functions.map {
    it.run {
      val `(unconstrainedTypeParams)` = `(typeParameters)`.map { "${it.name}".typeParameter.value }
      val `(givenParams)` = `(typeParameters)`.foldIndexed(ScopedList.empty<KtParameter>()) { n, params, typeParam ->
        val givenConstrain = typeParam.givenConstrain()?.toString()?.replace(givenAnnotation, "")
        if (givenConstrain != null) params + "given$n: @arrow.Given $givenConstrain = arrow.given".parameter
        else params
      }

      val `(paramsWithGiven)` = `(params)` + `(givenParams)`
      """
      public fun $`(unconstrainedTypeParams)` $receiver$name $`(paramsWithGiven)`$returnType =
          ${runScope(this, `(givenParams)`)}
      """.function(null).value
    }
  }, separator = "\n")

private fun ElementScope.runScope(namedFunction: NamedFunction, scopedList: ScopedList<KtParameter>): Scope<KtExpression> {
  val body = namedFunction.body
  return if (body != null)
    scopedList.value.fold(body.toString().expression) { acc, parameter ->
      """
      with<${parameter.typeReference?.text}, ${namedFunction.returnType.copy(prefix = "")}>(${parameter.name}) { $acc }
      """.expression
    }
  else Scope.empty<KtExpression>()
}

private fun KtTypeParameter.givenConstrain(): TypeReference? =
  if (containsGivenConstrain()) TypeReference(extendsBound) else null

private fun KtNamedFunction.containsGivenConstrain(): Boolean =
  typeParameters.any { typeParameter ->
    typeParameter.containsGivenConstrain()
  }

private fun KtTypeParameter.containsGivenConstrain(): Boolean =
  extendsBound?.annotationEntries?.any { it.text.matches(givenAnnotation) } ?: false