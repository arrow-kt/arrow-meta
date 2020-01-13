package arrow.meta.plugins.proofs.phases.resolve.diagnostics

import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.Proof
import arrow.meta.plugins.proofs.phases.extensionProof
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters1
import org.jetbrains.kotlin.diagnostics.DiagnosticWithParameters2
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeReference
import org.jetbrains.kotlin.resolve.calls.inference.InferenceErrorData
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.typeUtil.isSubtypeOf
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

fun CompilerContext.suppressProvenTypeMismatch2(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.UNRESOLVED_REFERENCE_WRONG_RECEIVER &&
    diagnostic.safeAs<DiagnosticWithParameters1<PsiElement, Collection<ResolvedCall<*>>>>()?.let { diagnosticWithParameters ->
      false
    } == true

fun CompilerContext.suppressProvenTypeMismatch(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.TYPE_MISMATCH &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtExpression, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.b
      val superType = diagnosticWithParameters.a
      Log.Verbose({ "suppressProvenTypeMismatch: $subType, $superType, $this" }) {
        extensionProof(subType, superType) != null
      }
    } == true

fun CompilerContext.suppressTypeInferenceExpectedTypeMismatch(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.TYPE_INFERENCE_EXPECTED_TYPE_MISMATCH &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtElement, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.a
      val superType = diagnosticWithParameters.b
      Log.Verbose({ "suppressTypeInferenceExpectedTypeMismatch: $subType, $superType, $this" }) {
        extensionProof(subType, superType) != null
      }
    } == true

fun CompilerContext.suppressTypeArgumentNotWithinBounds(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.UPPER_BOUND_VIOLATED &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtTypeReference, KotlinType, KotlinType>>()?.let { diagnosticWithParameters ->
      val subType = diagnosticWithParameters.b
      val superType = diagnosticWithParameters.a
      Log.Verbose({ "suppressTypeArgumentNotWithinBounds: $subType, $superType, $this" }) {
        subType.isSubtypeOf(superType)
      }
    } == true

fun CompilerContext.suppressConstantExpectedTypeMismatch(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
  diagnostic.factory == Errors.CONSTANT_EXPECTED_TYPE_MISMATCH &&
    diagnostic.safeAs<DiagnosticWithParameters2<KtConstantExpression, String, KotlinType>>()?.let { diagnosticWithParameters ->
      val superType = diagnosticWithParameters.b
      val elementType = diagnosticWithParameters.psiElement.elementType.toString()
      val subType = when (elementType) {
        "INTEGER_CONSTANT" -> module?.builtIns?.intType
        "CHARACTER_CONSTANT" -> module?.builtIns?.charType
        "FLOAT_CONSTANT" -> module?.builtIns?.floatType
        "BOOLEAN_CONSTANT" -> module?.builtIns?.booleanType
        "NULL" -> module?.builtIns?.nullableAnyType
        else -> null
      }
      Log.Verbose({ "suppressConstantExpectedTypeMismatch: $subType, $superType, $this" }) {
        subType?.let {
          extensionProof(it, superType) != null
        }
      }
    } == true

//fun CompilerContext.suppressUpperboundViolated(diagnostic: Diagnostic, proofs: List<Proof>): Boolean =
//  diagnostic.factory == Errors.TYPE_INFERENCE_UPPER_BOUND_VIOLATED &&
//    diagnostic.safeAs<DiagnosticWithParameters1<PsiElement, InferenceErrorData>>()?.let { diagnosticWithParameters ->
//      val inferenceErrorData = diagnosticWithParameters.a
//      true
////      Log.Verbose({ "suppressUpperboundViolated: $subType, $superType, $this" }) {
////        proofs.subtypingProof(this, subType, superType) != null
////      }
//    } == true