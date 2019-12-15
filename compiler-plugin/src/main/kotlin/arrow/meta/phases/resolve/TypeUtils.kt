package arrow.meta.phases.resolve

import arrow.meta.phases.CompilerContext
import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofCandidate
import arrow.meta.proofs.ProofStrategy
import arrow.meta.proofs.isProof
import arrow.meta.proofs.typeSubstitutor
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutorByConstructorMap
import org.jetbrains.kotlin.resolve.calls.inference.components.substituteTypeVariable
import org.jetbrains.kotlin.resolve.calls.inference.model.TypeVariableFromCallableDescriptor
import org.jetbrains.kotlin.resolve.calls.smartcasts.SingleSmartCast
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.EnumValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.types.IntersectionTypeConstructor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewKotlinTypeCheckerImpl
import org.jetbrains.kotlin.types.getAbbreviation
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.asTypeProjection
import org.jetbrains.kotlin.types.typeUtil.builtIns
import org.jetbrains.kotlin.types.typeUtil.isNothing
import org.jetbrains.kotlin.types.typeUtil.isTypeParameter
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound
import org.jetbrains.kotlin.types.typeUtil.substitute
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.io.ComponentNameProvider
import org.jgrapht.io.DOTExporter
import java.io.StringWriter
import java.util.concurrent.ConcurrentHashMap

val baseLineTypeChecker: KotlinTypeChecker =
  NewKotlinTypeCheckerImpl(KotlinTypeRefiner.Default)

//    .run {
//    if (this.isTypeParameter()) builtIns.nullableAnyType
//    else this
//  }

fun ProofCandidate.provesWithBaselineTypeChecker(): Boolean =
  through.extensionReceiverParameter?.type?.let { receiver ->
    through.returnType?.let { returnType ->
      val result = baseLineTypeChecker.run {
        isSubtypeOf(subType, receiver) && isSubtypeOf(superType, returnType)
      }
      result
    }
  } ?: false

fun KotlinType.typeArgumentsMap(other: KotlinType): Map<TypeProjection, TypeProjection> =
  if (isTypeParameter()) mapOf(this.asTypeProjection() to other.asTypeProjection())
  else arguments.mapIndexed { n, typeProjection ->
    other.arguments.getOrNull(n)?.let {
      typeProjection to it
    }
  }.filterNotNull().toMap()

val KotlinType.unwrappedNotNullableType: UnwrappedType
  get() = makeNotNullable().unwrap()

val proofCache: ConcurrentHashMap<ModuleDescriptor, List<Proof>> = ConcurrentHashMap()

fun disposeProofCache(): Unit =
  proofCache.clear()

val ModuleDescriptor.typeProofs: List<Proof>
  get() =
    if (this is ModuleDescriptorImpl) {
      try {
        val cacheValue = proofCache[this]
        when {
          cacheValue != null -> {
            println("Serving cached value for $this: $cacheValue")
            cacheValue
          }
          else -> emptyList()
        }
      } catch (e: RuntimeException) {
        println("TODO() Detected exception: ${e.printStackTrace()}")
        emptyList<Proof>()
      }
    } else emptyList()

fun CompilerContext.cachedModule(): ModuleDescriptor? =
  proofCache.keys.firstOrNull { it.name == module.name }

fun cachedModule(name: Name): ModuleDescriptor? =
  proofCache.keys.firstOrNull { it.name == name }

fun ModuleDescriptor.initializeProofCache(): List<Proof> =
  try {
    val moduleProofs: List<Proof> = computeModuleProofs()
    if (moduleProofs.isNotEmpty()) { //remove old cached modules if this the same kind and has more recent proofs
      cachedModule(name)?.let { proofCache.remove(it) }
      proofCache[this] = moduleProofs
    }
    moduleProofs
  } catch (e: Throwable) {
    emptyList()
  }

private fun ModuleDescriptor.computeModuleProofs(): List<Proof> =
  (getSubPackagesOf(FqName.ROOT) { true })
    .filter { !it.isRoot }
    .flatMap { packageName ->
      getPackage(packageName).memberScope
        .getContributedDescriptors { true }
        .filterIsInstance<PackageViewDescriptor>()
        .flatMap { packageViewDescriptor ->
          packageViewDescriptor
            .memberScope
            .getContributedDescriptors { true }
            .filterIsInstance<FunctionDescriptor>()
            .filter(FunctionDescriptor::isProof)
        }.mapNotNull(FunctionDescriptor::asProof)
    }.apply {
      val module = this@computeModuleProofs
      println("Recomputed cache: $module proofs: ${size}, module cache size: ${proofCache.size}")
    }.synthetic()

fun List<SimpleFunctionDescriptor>.toSynthetic(): List<SimpleFunctionDescriptor> =
  mapNotNull { it.synthetic() }

//    .run {
//    newCopyBuilder().setDispatchReceiverParameter(extensionReceiverParameter).build()
//  }

inline fun <reified C : CallableMemberDescriptor> C.synthetic(): C =
  copy(
    containingDeclaration,
    modality,
    if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    true
  ) as C

fun List<Proof>.synthetic(): List<Proof> =
  mapNotNull { proof ->
    Proof(proof.from, proof.to, (proof.through as SimpleFunctionDescriptor).synthetic(), proof.proofType)
  }

class ProofVertex(val type: KotlinType) {
  override fun equals(other: Any?): Boolean =
    if (other is ProofVertex) {
      baseLineTypeChecker.isSubtypeOf(type, other.type)
    } else false

  override fun hashCode(): Int {
    return type.hashCode()
  }
}

/**
 * Returns an intersection of this [KotlinType] with [other]
 */
fun KotlinType.intersection(vararg other: KotlinType): KotlinType {
  val constructor = IntersectionTypeConstructor(listOf(this) + other.toList())
  return KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(
    Annotations.EMPTY,
    constructor,
    emptyList(),
    false,
    constructor.createScopeForKotlinType()
  )
}

fun KotlinType.`isSubtypeOf(NewKotlinTypeChecker)`(other: KotlinType): Boolean =
  baseLineTypeChecker.isSubtypeOf(this, other)

fun BindingTrace.applySmartCast(
  call: Call,
  expression: KtExpression,
  to: KotlinType
) {
  val smartCast = SingleSmartCast(call, to)
  record(BindingContext.SMARTCAST, expression, smartCast)
  //ExpressionReceiver.create(expression, to, bindingContext)
}

val ModuleDescriptor.typeProofsGraph: Graph<ProofVertex, Proof>
  get() {
    val g: Graph<ProofVertex, Proof> = DefaultDirectedGraph(Proof::class.java)
    val proofs = typeProofs
    val vertices = proofs.flatMap { listOf(it.from, it.to) }
    vertices.forEach { g.addVertex(ProofVertex(it)) }
    proofs.forEach { g.addEdge(ProofVertex(it.from), ProofVertex(it.to), it) }
    return g
  }

/**
 * Render a graph in DOT format.
 *
 * @param hrefGraph a graph based on URI objects
 */
fun Graph<ProofVertex, Proof>.dump() {
  val vertexIdProvider = ComponentNameProvider<ProofVertex> { """"${it.type.getAbbreviation()}"""" }
  val edgeLabelProvider = ComponentNameProvider<Proof> { """"${it.through.fqNameSafe.asString()}"""" }
  val exporter = DOTExporter(vertexIdProvider, vertexIdProvider, edgeLabelProvider)
  val writer = StringWriter()
  exporter.exportGraph(this, writer);
  println(writer.toString());
}


fun FunctionDescriptor.asProof(): Proof? =
  extensionReceiverParameter?.type?.let { from ->
    returnType?.let { to ->
      val annotationArgs = annotations.first().allValueArguments
      val value: ArrayValue? = annotationArgs[Name.identifier("of")] as? ArrayValue
      val proofStrategy =
        when ((value?.value?.getOrNull(0) as? EnumValue)?.enumEntryName?.asString()) {
          ProofStrategy.Subtyping.name -> ProofStrategy.Subtyping
          else -> ProofStrategy.Extension
        }
      Proof(from, to, this, proofStrategy)
    }
  }

