package arrow.meta.internal

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames

// copied from stdlib without A : Any

fun <T> Iterable<T?>.filterNotNull(): List<T> =
  filterNotNullTo(ArrayList())

fun <C : MutableCollection<in T>, T> Iterable<T?>.filterNotNullTo(destination: C): C {
  for (element in this) if (element != null) destination.add(element)
  return destination
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapNotNullTo(destination: C, transform: (T) -> R?): C {
  forEach { element -> transform(element)?.let { destination.add(it) } }
  return destination
}

inline fun <T, R> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
  return mapNotNullTo(ArrayList<R>(), transform)
}

inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.mapNotNullTo(destination: C, transform: (Map.Entry<K, V>) -> R?): C {
  forEach { element -> transform(element)?.let { destination.add(it) } }
  return destination
}

inline fun <K, V, R> Map<out K, V>.mapNotNull(transform: (Map.Entry<K, V>) -> R?): List<R> {
  return mapNotNullTo(ArrayList<R>(), transform)
}

data class SealedSubclass(val simpleName: Name, val fqName: FqName?, val typeVariables: List<String>)

fun KtClass.sealedSubclasses(): List<SealedSubclass> =
  innerSealedSubclasses() + outerSealedSubclasses()

fun List<KtDeclaration>.sealedVariants(superKt: KtClass): List<SealedSubclass> =
  filter {
    (it is KtClassOrObject) && it.getSuperNames().contains(superKt.nameAsSafeName.identifier)
  }.map { it as KtClassOrObject }.map {
    SealedSubclass(
      simpleName = it.nameAsSafeName,
      fqName = it.fqName,
      typeVariables = if (it is KtClass) it.renderTypeParameters else emptyList()
    )
  }

fun KtClass.innerSealedSubclasses(): List<SealedSubclass> =
  declarations.sealedVariants(this)

fun KtClass.outerSealedSubclasses(): List<SealedSubclass> =
  containingKtFile.declarations.sealedVariants(this)

val KtClass.renderTypeParameters: List<String>
  get() = typeParameters.map { it.nameAsSafeName.identifier }.map {
    it.replace("out ", "")
      .replace("in ", "")
  }

/**
 * From Eugenio's https://github.com/Takhion/kotlin-metadata
 * If this [isNotBlank] then it adds the optional [prefix] and [postfix].
 */
fun String.plusIfNotBlank(
  prefix: String = "",
  postfix: String = ""
) =
  if (isNotBlank()) "$prefix${this}$postfix" else this
