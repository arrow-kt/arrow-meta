package arrow.meta.plugins.higherkind

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.ktClassNamed
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun Diagnostic.kindsTypeMismatch(): Boolean =
  factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH &&
    safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val a = diagnosticWithParameters.a
      val b = diagnosticWithParameters.b
      //if this is the kind type checker then it will do the right thing otherwise this proceeds as usual with the regular type checker
      KotlinTypeChecker.DEFAULT.isSubtypeOf(a, b)
    } == true

fun ScopedList<KtTypeParameter>.invariant(constrained: Boolean = false): String =
  value.joinToString {
    it.text
      .replace("out ", "")
      .replace("in ", "").let { s ->
        if (constrained) s
        else s.replace("(.*):(.*)".toRegex(), "$1")
      }.trim()
  }

val KtClass.partialTypeParameters: String
  get() = typeParameters
    .dropLast(1)
    .joinToString(separator = ", ") {
      it.nameAsSafeName.identifier
    }

val KtClass.arity: Int
  get() = typeParameters.size

val KtClass.kindAritySuffix: String
  get() = arity.let { if (it > 1) "$it" else "" }

val KtClass.partialKindAritySuffix: String
  get() = (arity - 1).let { if (it > 1) "$it" else "" }

fun isHigherKindedType(ktClass: KtClass): Boolean =
  ktClass.isAnnotatedWith(higherKindAnnotation) &&
    ktClass.fqName?.asString()?.startsWith("arrow.Kind") != true &&
    !ktClass.isAnnotation() &&
    !ktClass.isNested() &&
    !ktClass.superTypeIsSealedInFile() &&
    ktClass.typeParameters.isNotEmpty() &&
    ktClass.parent is KtFile

val higherKindAnnotation: Regex = Regex("@(arrow\\.)?higherkind")

private fun KtClass.superTypeIsSealedInFile(): Boolean =
  superTypeListEntries.isNotEmpty() &&
    superTypeListEntries.any {
      val className = it.text?.substringBefore("<")
      it.containingKtFile.ktClassNamed(className) != null
    }

private fun KtClass.isNested(): Boolean =
  parent is KtClassOrObject

val kindName: FqName = FqName("arrow.Kind")

val FqName.kindTypeAliasName: Name
  get() {
    val segments = pathSegments()
    val simpleName = segments.lastOrNull() ?: Name.special("index not ready")
    return Name.identifier("${simpleName}Of")
  }
