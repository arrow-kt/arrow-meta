package arrow.meta.phases.resolve

import arrow.meta.proofs.Proof
import arrow.meta.proofs.ProofStrategy
import arrow.meta.proofs.isProof
import arrow.meta.quotes.get
import org.jetbrains.kotlin.config.KotlinTypeRefinerImpl
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.smartcasts.SingleSmartCast
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.constants.EnumValue
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProvider
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor
import org.jetbrains.kotlin.types.IntersectionTypeConstructor
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker
import org.jetbrains.kotlin.types.checker.KotlinTypeRefiner
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.checker.NewKotlinTypeCheckerImpl
import org.jetbrains.kotlin.types.getAbbreviation
import org.jetbrains.kotlin.types.replace
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

fun KotlinType.replaceTypeArgsWithUpperbounds(): KotlinType =
  replace(arguments.map { typeProjection ->
    typeProjection.substitute {
      TypeUtils.getTypeParameterDescriptorOrNull(it)?.representativeUpperBound ?: it
    }
  })

fun CallableDescriptor.provesWithBaselineTypeChecker(from: KotlinType, to: KotlinType): Boolean =
  extensionReceiverParameter?.type?.let { receiver ->
    returnType?.let { returnType ->
      val receiverWithUpperBounds = receiver.replaceTypeArgsWithUpperbounds()
      val returnTypeWithUpperBounds = returnType.replaceTypeArgsWithUpperbounds()
      val result = baseLineTypeChecker.run {
        isSubtypeOf(from, receiverWithUpperBounds) && isSubtypeOf(to, returnTypeWithUpperBounds)
      }
      result
    }
  } ?: false

fun KotlinType.typeArgumentsMap(other: KotlinType): Map<TypeProjection, TypeProjection> =
  arguments.mapIndexed { n, typeProjection ->
    other.arguments.getOrNull(n)?.let {
      typeProjection to it
    }
  }.filterNotNull().toMap()

val KotlinType.unwrappedNotNullableType: UnwrappedType
  get() = makeNotNullable().unwrap()

val proofCache: ConcurrentHashMap<Name, Pair<ModuleDescriptor, List<Proof>>> = ConcurrentHashMap()

fun disposeProofCache(): Unit =
  proofCache.clear()

val ModuleDescriptor.typeProofs: List<Proof>
  get() =
    if (this is ModuleDescriptorImpl) {
      try {
        val cacheValue = proofCache[name]
        val packageFragmentProvider: PackageFragmentProvider? = this["packageFragmentProviderForModuleContent"]
        if (packageFragmentProvider != null) {
          //initializeProofCache()
        }
        when {
          cacheValue != null -> {
            println("Serving cached value for $this: ${cacheValue.second}")
            cacheValue.second
          }
          else -> emptyList()
        }
      } catch (e: RuntimeException) {
        println("TODO() Detected exception: ${e.printStackTrace()}")
        emptyList<Proof>()
      }
    } else emptyList()

fun ModuleDescriptor.initializeProofCache(): List<Proof> =
  try {
    val moduleProofs: List<Proof> = computeModuleProofs()
    if (moduleProofs.isNotEmpty()) {
      proofCache[module.name] = module to moduleProofs
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

inline fun List<SimpleFunctionDescriptor>.toSynthetic(): List<SimpleFunctionDescriptor> =
  map { it.synthetic() }

inline fun SimpleFunctionDescriptor.synthetic(): SimpleFunctionDescriptor =
  SimpleFunctionDescriptorImpl.create(
    containingDeclaration,
    Annotations.EMPTY,
    name,
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    source
  )

inline fun <reified C : CallableMemberDescriptor> C.synthetic(): C =
  copy(
    containingDeclaration,
    modality,
    if (visibility == Visibilities.INHERITED) Visibilities.PUBLIC else visibility,
    CallableMemberDescriptor.Kind.SYNTHESIZED,
    true
  ) as C

fun List<Proof>.synthetic(): List<Proof> =
  map {
    Proof(it.from, it.to, (it.through as SimpleFunctionDescriptor).synthetic(), it.proofType)
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
      val descriptor = when (proofStrategy) {
        ProofStrategy.Extension ->
          this.newCopyBuilder()
            .setDispatchReceiverParameter(this.dispatchReceiverParameter)
            .setOwner(this.containingDeclaration)
            .build() ?: this
        else -> this
      }
      Proof(from, to, descriptor, proofStrategy)
    }
  }

