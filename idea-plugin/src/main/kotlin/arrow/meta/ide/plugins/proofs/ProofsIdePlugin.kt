package arrow.meta.ide.plugins.proofs

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.dsl.platform.ide
import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.resolve.intersection
import arrow.meta.phases.resolve.typeProofs
import arrow.meta.plugins.comprehensions.isBinding
import arrow.meta.proofs.Proof
import arrow.meta.proofs.intersection
import arrow.meta.proofs.suppressProvenTypeMismatch
import arrow.meta.proofs.suppressUpperboundViolated
import arrow.meta.quotes.get
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor
import org.jetbrains.kotlin.descriptors.impl.ReceiverParameterDescriptorImpl
import org.jetbrains.kotlin.js.resolve.diagnostics.findPsi
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtDeclarationWithBody
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.blockExpressionsOrSingle
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.FunctionDescriptorUtil
import org.jetbrains.kotlin.resolve.OverloadChecker
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.calls.results.TypeSpecificityComparator
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.SingleSmartCast
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeImpl
import org.jetbrains.kotlin.resolve.scopes.LexicalScopeKind
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ExtensionReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.SimpleType
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

@Suppress("UnstableApiUsage")
val IdeMetaPlugin.proofsIdePlugin: Plugin
  get() = "ProofsIdePlugin" {
    meta(
      addLineMarkerProvider(
        icon = ArrowIcons.POLY,
        transform = { it.safeAs<KtNamedFunction>()?.takeIf{
          true
        }?.identifyingElement },
        message = { "Bind" }
      ),
//      addDiagnosticSuppressor { diagnostic ->
//        if (diagnostic.factory == Errors.UNRESOLVED_REFERENCE) {
//          Errors.UNRESOLVED_REFERENCE.cast(diagnostic).let {
//            module.typeProofs.extensions().any { ext ->
//              ext.to.memberScope.getContributedDescriptors { true }.any {
//                it.name.asString() == diagnostic.psiElement.text
//              }
//            }
//          }
//        } else false
//      },
      addDiagnosticSuppressor { it.suppressProvenTypeMismatch(module.typeProofs) },
      addDiagnosticSuppressor { it.suppressUpperboundViolated(module.typeProofs) }
      //ideSyntheticBodyResolution()
    )
  }

private fun Meta.ideSyntheticBodyResolution(): ExtensionPhase = ide {
  syntheticResolver(
    generateSyntheticMethods = { thisDescriptor, name, bindingContext, fromSupertypes, result ->
      println("generateSyntheticMethods($thisDescriptor, $name, $bindingContext, $fromSupertypes, $result)")
      thisDescriptor.safeAs<LazyClassDescriptor>()?.let { lazyDescriptor ->
        val lazyClassContext: Any = lazyDescriptor["c"]
        lazyClassContext.safeAs<ResolveSession>()?.let { session ->
          result.firstOrNull { it.name == name }?.let { function ->
            resolveBodyWithExtensionsScope(session, function)
          }
        }
      }
    }
  )
} ?: ExtensionPhase.Empty

private fun CompilerContext.resolveBodyWithExtensionsScope(session: ResolveSession, function: SimpleFunctionDescriptor): Unit {
  val proofs = module.typeProofs
  function.findPsi().safeAs<KtDeclarationWithBody>()?.let { ktCallable ->
    println("resolveBodyWithExtensionsScope: ${ktCallable.text}")
    val functionScope = session.declarationScopeProvider.getResolutionScopeForDeclaration(ktCallable)
    val innerScope = FunctionDescriptorUtil.getFunctionInnerScope(functionScope, function, session.trace, OverloadChecker(TypeSpecificityComparator.NONE))
    val bodyResolver = analyzer?.createBodyResolver(
      session, session.trace, ktCallable.containingKtFile, StatementFilter.NONE
    )
//    val referencedTypes = function.referencedProofedCalls(proofs)
//    referencedTypes.forEach { (resolvedCall, typeProofs) ->
//      typeProofs.forEach { proof ->
//        val intersection = proof.intersection(/* TODO type susbtitutor? */).asSimpleType()
//        applySmartCast(resolvedCall.call, intersection, ktCallable, session)
//        println("applySmartCast(${resolvedCall.call}, $intersection, $ktCallable, $session)")
//      }
//    }
    val modifiedScope = proofs.lexicalScope(innerScope, function)
    bodyResolver?.resolveFunctionBody(DataFlowInfo.EMPTY, session.trace, ktCallable, function, modifiedScope)
//    val calls = session.trace.bindingContext.getSliceContents(BindingContext.CALL)
//    calls.forEach(::println)
//    modifiedScope.implicitReceiver?.type?.asSimpleType()?.let {
//      applySmartCast(null, it, ktCallable, session)
//    }
  }
}

fun List<Proof>.mentioning(kotlinType: KotlinType): List<Proof> =
  filter { it.from.constructor == kotlinType.constructor && it.to.constructor == kotlinType.constructor }

//fun Call.inProof(proofs: List<Proof>): List<Proof> {
//  val type = this.call
//  return proofs.filter { proof ->
//    type.`isSubtypeOf(NewKotlinTypeChecker)`(proof.from) ||
//      type.`isSubtypeOf(NewKotlinTypeChecker)`(proof.to)
//  }
//}
//
//private fun SimpleFunctionDescriptor.referencedProofedCalls(proofs: List<Proof>, bindingContext: BindingContext): List<Pair<ResolvedCall<*>, List<Proof>>> =
//  findPsi()
//    ?.collectDescendantsOfType<KtCallExpression> { true }
//    ?.mapNotNull { it.getCall(bindingContext) }
//    ?.map { it to it.inProof(proofs) }
//    .orEmpty()

private fun applySmartCast(
  call: Call?,
  uberExtendedType: SimpleType,
  ktCallable: KtExpression,
  session: ResolveSession
) {
  val smartCast = SingleSmartCast(call, uberExtendedType)
  ktCallable.blockExpressionsOrSingle().filterIsInstance<KtExpression>().firstOrNull()?.let { expression ->
    session.trace.record(BindingContext.SMARTCAST, expression, smartCast)
    ExpressionReceiver.create(expression, uberExtendedType, session.trace.bindingContext)
  }
}

private fun Proof.lexicalScope(currentScope: LexicalScope, containingDeclaration: DeclarationDescriptor): LexicalScope {
  val proofIntersection = from.intersection(to)
  val ownerDescriptor = AnonymousFunctionDescriptor(containingDeclaration, Annotations.EMPTY, CallableMemberDescriptor.Kind.DECLARATION, SourceElement.NO_SOURCE, false)
  val extensionReceiver = ExtensionReceiver(ownerDescriptor, proofIntersection, null)
  val extensionReceiverParamDescriptor = ReceiverParameterDescriptorImpl(ownerDescriptor, extensionReceiver, ownerDescriptor.annotations)
  ownerDescriptor.initialize(extensionReceiverParamDescriptor, null, through.typeParameters, through.valueParameters, through.returnType, Modality.FINAL, through.visibility)
  return LexicalScopeImpl(currentScope, ownerDescriptor, true, extensionReceiverParamDescriptor, LexicalScopeKind.FUNCTION_INNER_SCOPE)
}

private fun List<Proof>.lexicalScope(currentScope: LexicalScope, containingDeclaration: CallableMemberDescriptor): LexicalScope {
  val types = map { it.intersection(/* TODO substitutor */) }
  return if (types.isEmpty()) currentScope
  else types.reduce { acc, kotlinType -> acc.intersection(kotlinType) }.let { proofIntersection ->
    val ownerDescriptor = AnonymousFunctionDescriptor(containingDeclaration, Annotations.EMPTY, CallableMemberDescriptor.Kind.DECLARATION, SourceElement.NO_SOURCE, false)
    val extensionReceiver = ExtensionReceiver(ownerDescriptor, proofIntersection, null)
    val extensionReceiverParamDescriptor = ReceiverParameterDescriptorImpl(ownerDescriptor, extensionReceiver, ownerDescriptor.annotations)
    ownerDescriptor.initialize(extensionReceiverParamDescriptor, null, emptyList(), emptyList(), ownerDescriptor.returnType, null, Visibilities.PUBLIC)
    LexicalScopeImpl(currentScope, ownerDescriptor, true, extensionReceiverParamDescriptor, LexicalScopeKind.FUNCTION_INNER_SCOPE)
  }
}
