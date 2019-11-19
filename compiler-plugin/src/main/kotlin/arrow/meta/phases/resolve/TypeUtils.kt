package arrow.meta.phases.resolve

import arrow.meta.proofs.Proof
import arrow.meta.proofs.isProof
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeProjection
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.NewKotlinTypeChecker
import org.jetbrains.kotlin.types.replace
import org.jetbrains.kotlin.types.typeUtil.makeNotNullable
import org.jetbrains.kotlin.types.typeUtil.representativeUpperBound
import org.jetbrains.kotlin.types.typeUtil.substitute

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
      val result = NewKotlinTypeChecker.run {
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

val ModuleDescriptor.typeProofs: List<Proof>
  get() = (getSubPackagesOf(FqName.ROOT) { true } + FqName.ROOT).flatMap { packageName ->
    getPackage(packageName).fragments.flatMap { packageFragmentDescriptor ->
      println("Scanning package: ${packageFragmentDescriptor.fqName}")
      packageFragmentDescriptor
        .getMemberScope()
        .getContributedDescriptors { true }
        .filterIsInstance<FunctionDescriptor>()
        .filter(FunctionDescriptor::isProof)
    }.mapNotNull(FunctionDescriptor::asProof)
  }

fun FunctionDescriptor.asProof(): Proof? =
  extensionReceiverParameter?.type?.let { from ->
    returnType?.let { to ->
      Proof(from, to, this)
    }
  }

