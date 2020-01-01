package arrow.meta.plugins.proofs

/*
Type Proofs are injective proofs from A -> B
Talk about Curry Howard Correspondence
 */

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.log.Log
import arrow.meta.log.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.ElementScope
import arrow.meta.phases.analysis.dfs
import arrow.meta.phases.resolve.initializeProofCache
import arrow.meta.proofs.MetaFileScopeProvider
import arrow.meta.proofs.ProofTypeChecker
import arrow.meta.proofs.ProofsBodyResolveContent
import arrow.meta.proofs.ProofsPackageFragmentDescriptor
import arrow.meta.proofs.suppressConstantExpectedTypeMismatch
import arrow.meta.proofs.suppressProvenTypeMismatch
import arrow.meta.proofs.suppressTypeInferenceExpectedTypeMismatch
import arrow.meta.proofs.suppressUpperboundViolated
import arrow.meta.proofs.syntheticMemberFunctions
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
import org.jetbrains.kotlin.analyzer.common.CommonPlatformAnalyzerServices
import org.jetbrains.kotlin.config.LanguageVersionSettingsImpl
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.context.SimpleGlobalContext
import org.jetbrains.kotlin.context.withModule
import org.jetbrains.kotlin.context.withProject
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.frontend.di.createContainerForBodyResolve
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.CommonPlatforms
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.BodyResolver
import org.jetbrains.kotlin.resolve.LazyTopDownAnalyzer
import org.jetbrains.kotlin.resolve.ModuleStructureOracle
import org.jetbrains.kotlin.resolve.StatementFilter
import org.jetbrains.kotlin.resolve.TopDownAnalysisMode
import org.jetbrains.kotlin.resolve.calls.ArgumentTypeResolver
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.scopes.receivers.TransientReceiver
import org.jetbrains.kotlin.types.FlexibleTypeImpl
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.asSimpleType

val givenAnnotation: Regex = Regex("@(arrow\\.)?given")

val Meta.typeProofs: Plugin
  get() =
    "Type Proofs" {
      meta(
        enableIr(),
        file(KtFile::containsGivenConstrains) {
          Transform.newSources(
            """
              $importList
              ${generateGivenSupportingFunctions(givenConstrainedDeclarations())}
            """.formatCode().file("Extensions." + name)
          )
        },
        packageFragmentProvider { project, module, storageManager, trace, moduleInfo, lookupTracker ->
          object : PackageFragmentProvider {

            override fun getPackageFragments(fqName: FqName): List<PackageFragmentDescriptor> =
              Log.Verbose({ "packageFragmentProvider.getPackageFragments $fqName" }) {
                listOf(ProofsPackageFragmentDescriptor(module, fqName) { module.typeProofs })
              }


            override fun getSubPackagesOf(fqName: FqName, nameFilter: (Name) -> Boolean): Collection<FqName> =
              Log.Verbose({ "packageFragmentProvider.getSubPackagesOf $fqName" }) {
                emptyList()
              }
          }
        },
        syntheticScopes(
          syntheticMemberFunctionsForName = { types, name, location ->
            Log.Verbose({ "syntheticScopes.syntheticMemberFunctionsForName $types $name $this" }) {
              val proofs = module?.typeProofs.orEmpty()
              proofs.syntheticMemberFunctions(types, name)
            }
          },
          syntheticMemberFunctions = { types ->
            Log.Verbose({ "syntheticScopes.syntheticMemberFunctions $types $this" }) {
              module?.typeProofs?.syntheticMemberFunctions(types).orEmpty()
            }
          },
          syntheticStaticFunctions = { scope ->
            Log.Verbose({ "syntheticScopes.syntheticStaticFunctions $scope $this" }) {
              emptyList()
            }
          },
          syntheticStaticFunctionsForName = { scope, name, location ->
            Log.Verbose({ "syntheticScopes.syntheticStaticFunctionsForName $scope $name $location $this" }) {
              emptyList()
            }
          },
          syntheticConstructor = { constructor ->
            Log.Verbose({ "syntheticScopes.syntheticConstructor $constructor" }) {
              null
            }
          },
          syntheticConstructors = { scope ->
            Log.Verbose({ "syntheticScopes.syntheticConstructors $scope" }) {
              emptyList()
            }
          },
          syntheticConstructorsForName = { scope, name, location ->
            Log.Verbose({ "syntheticScopes.syntheticConstructorsForName $scope $name, $location" }) {
              emptyList()
            }
          },
          syntheticExtensionProperties = { receiverTypes, location ->
            Log.Verbose({ "syntheticScopes.syntheticExtensionProperties $receiverTypes, $location" }) {
              emptyList()
            }
          },
          syntheticExtensionPropertiesForName = { receiverTypes, name, location ->
            Log.Verbose({ "syntheticScopes.syntheticExtensionPropertiesForName $receiverTypes, $name, $location" }) {
              emptyList()
            }
          }
        ),
        analysis(
          doAnalysis = { project, module, projectContext, files, bindingTrace, componentProvider ->
            module.initializeProofCache()
            val argumentTypeResolver: ArgumentTypeResolver = componentProvider.get()
            val typeCheckerField = ArgumentTypeResolver::class.java.getDeclaredField("kotlinTypeChecker").also { it.isAccessible = true }
            typeCheckerField.set(argumentTypeResolver, ProofTypeChecker(this))
            null
          }
        ),
//          analysisCompleted = { project, module, bindingTrace, files ->
//            //            val calls = bindingTrace.bindingContext.getSliceContents(BindingContext.CALL)
////            module.typeProofs.forEach {
////              calls.forEach { ktElement, call ->
////                val resolvedCall = call.getResolvedCall(bindingTrace.bindingContext)
////                val callReturnType = resolvedCall?.getReturnType()
////                if (callReturnType != null && !callReturnType.isNothing() && !callReturnType.isError && callReturnType.`isSubtypeOf(NewKotlinTypeChecker)`(it.from)) {
////                  if (ktElement is KtExpression) {
////                    val intersection = it.to.intersection(callReturnType)
////                    println("Smart cast $call for ${ktElement.text} with $callReturnType type: $intersection")
////                    bindingTrace.applySmartCast(call, ktElement, intersection) //TODO apply this in the synth resolution instead as the type classes plugin does and same for the IDE
////                  }
////                }
////              }
////            }
//            null
//          }
//        ),
        suppressDiagnostic { this.suppressProvenTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { this.suppressConstantExpectedTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { this.suppressTypeInferenceExpectedTypeMismatch(it, module.typeProofs) },
        suppressDiagnostic { this.suppressUpperboundViolated(it, module.typeProofs) },
        suppressDiagnostic { this.suppressTypeInferenceExpectedTypeMismatch(it, module.typeProofs) },
        typeChecker { ProofTypeChecker(this) },
//        irDump(),
        irCall { insertCallProofs(module.typeProofs, it) },
        irProperty { insertProof(module.typeProofs, it) },
        irVariable { insertProof(module.typeProofs, it) },
        irReturn { insertProof(module.typeProofs, it) },
        irDump()
      )
    }

private fun KtFile.containsGivenConstrains(): Boolean =
  givenConstrainedDeclarations().isNotEmpty()

private fun File.givenConstrainedDeclarations(): List<NamedFunction> =
  value.givenConstrainedDeclarations().map { NamedFunction(it) }

private fun KtFile.givenConstrainedDeclarations(): List<KtNamedFunction> =
  dfs {
    it is KtNamedFunction && it.containsGivenConstrain()
  }.filterIsInstance<KtNamedFunction>()

private fun ElementScope.generateGivenSupportingFunctions(functions: List<NamedFunction>): ScopedList<KtNamedFunction> =
  ScopedList(functions.map {
    it.run {
      val `(unconstrainedTypeParams)` = `(typeParameters)`.map { "${it.name}".typeParameter.value }
      val `(givenParams)` = `(typeParameters)`.foldIndexed(ScopedList.empty<KtParameter>()) { n, params, typeParam ->
        val givenConstrain = typeParam.givenConstrain()?.toString()?.replace(givenAnnotation, "")
        if (givenConstrain != null) params + "given$n: @arrowx.given $givenConstrain = arrow.given".parameter
        else params
      }

      val `(paramsWithGiven)` = `(params)` + `(givenParams)`
      """
      public fun $`(unconstrainedTypeParams)` $receiver$name $`(paramsWithGiven)`$returnType =
          ${runScope(this, `(givenParams)`)}
      """.function.value
    }
  }, separator = lineSeparator)

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
