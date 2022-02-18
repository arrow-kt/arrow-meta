@file:OptIn(SymbolInternals::class)

package arrow.meta.internal

import java.util.LinkedList
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.extensions.generatedDeclarationsSymbolProvider
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.scopes.FirContainingNamesAwareScope
import org.jetbrains.kotlin.fir.scopes.FirKotlinScopeProvider
import org.jetbrains.kotlin.fir.scopes.FirScopeProvider
import org.jetbrains.kotlin.fir.scopes.getFunctions
import org.jetbrains.kotlin.fir.scopes.getProperties
import org.jetbrains.kotlin.fir.scopes.getSingleClassifier
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.psiUtil.getSuperNames
import org.jetbrains.kotlin.resolve.scopes.ChainedMemberScope
import org.jetbrains.kotlin.resolve.scopes.MemberScope

// copied from stdlib without A : Any

fun <T> Iterable<T?>.filterNotNull(): List<T> = filterNotNullTo(ArrayList())

fun <C : MutableCollection<in T>, T> Iterable<T?>.filterNotNullTo(destination: C): C {
  for (element in this) if (element != null) destination.add(element)
  return destination
}

inline fun <T, R, C : MutableCollection<in R>> Iterable<T>.mapNotNullTo(
  destination: C,
  transform: (T) -> R?
): C {
  forEach { element -> transform(element)?.let { destination.add(it) } }
  return destination
}

inline fun <T, R> Iterable<T>.mapNotNull(transform: (T) -> R?): List<R> {
  return mapNotNullTo(ArrayList<R>(), transform)
}

inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.mapNotNullTo(
  destination: C,
  transform: (Map.Entry<K, V>) -> R?
): C {
  forEach { element -> transform(element)?.let { destination.add(it) } }
  return destination
}

inline fun <K, V, R> Map<out K, V>.mapNotNull(transform: (Map.Entry<K, V>) -> R?): List<R> {
  return mapNotNullTo(ArrayList<R>(), transform)
}

data class SealedSubclass(
  val simpleName: Name,
  val fqName: FqName?,
  val typeVariables: List<String>
)

fun KtClass.sealedSubclasses(): List<SealedSubclass> =
  innerSealedSubclasses() + outerSealedSubclasses()

fun List<KtDeclaration>.sealedVariants(superKt: KtClass): List<SealedSubclass> =
  filter {
    (it is KtClassOrObject) && it.getSuperNames().contains(superKt.nameAsSafeName.identifier)
  }
    .map { it as KtClassOrObject }
    .map {
      SealedSubclass(
        simpleName = it.nameAsSafeName,
        fqName = it.fqName,
        typeVariables = if (it is KtClass) it.renderTypeParameters else emptyList()
      )
    }

fun KtClass.innerSealedSubclasses(): List<SealedSubclass> = declarations.sealedVariants(this)

fun KtClass.outerSealedSubclasses(): List<SealedSubclass> =
  containingKtFile.declarations.sealedVariants(this)

val KtClass.renderTypeParameters: List<String>
  get() =
    typeParameters.map { it.nameAsSafeName.identifier }.map {
      it.replace("out ", "").replace("in ", "")
    }

/**
 * From Eugenio's https://github.com/Takhion/kotlin-metadata If this [isNotBlank] then it adds the
 * optional [prefix] and [postfix].
 */
fun String.plusIfNotBlank(prefix: String = "", postfix: String = "") =
  if (isNotBlank()) "$prefix${this}$postfix" else this

fun FirSession.gather2(
  initialPackages: List<FqName> = listOf(FqName("")),
  addSubPackages: Boolean,
  predicate: (FirElement) -> Boolean
) {
  val symbolProvider = generatedDeclarationsSymbolProvider

  val packagesWorklist = LinkedList(initialPackages)
  val scopesWorklist = LinkedList<FirContainingNamesAwareScope>()
  val result = mutableListOf<FirElement>()

  val scopeProvider: FirScopeProvider = FirKotlinScopeProvider()

  while (true) {
    if (scopesWorklist.isNotEmpty()) {
      val scope = scopesWorklist.remove()
      val callableNames = scope.getCallableNames()
      val classifierNames = scope.getClassifierNames()
      val functions: List<FirSimpleFunction> =
        callableNames.flatMap { scope.getFunctions(it) }.map { it.fir }
      val classes: List<FirDeclaration> =
        classifierNames.map { scope.getSingleClassifier(it) }.mapNotNull { it?.fir }
      val properties: List<FirVariable> =
        callableNames.flatMap { scope.getProperties(it) }.mapNotNull { it.fir }

      result.addAll(functions.filter(predicate))
      result.addAll(classes.filter(predicate))
      result.addAll(properties.filter(predicate))

      val scopeSession = ScopeSession()

      scopesWorklist.addAll(
        classes.flatMap {
          when (it) {
            is FirClass ->
              listOfNotNull(
                it.scopeProvider.getNestedClassifierScope(it, this, scopeSession),
                it.scopeProvider.getStaticScope(it, this, scopeSession),
                it.scopeProvider.getStaticMemberScopeForCallables(it, this, scopeSession),
                it.scopeProvider.getUseSiteMemberScope(it, this, scopeSession),
              )
            else -> emptyList()
          }
        }
      )
    }
  }
}

fun ModuleDescriptor.gather(
  initialPackages: List<FqName> = listOf(FqName("")),
  addSubPackages: Boolean,
  predicate: (DeclarationDescriptor) -> Boolean
): List<DeclarationDescriptor> {
  // initialize worklists
  val packagesWorklist = LinkedList(initialPackages)
  val scopesWorklist = LinkedList<MemberScope>()
  // initialize place for results
  val result = mutableListOf<DeclarationDescriptor>()

  // the work
  while (true) {
    if (scopesWorklist.isNotEmpty()) {
      // work to do in a member scope
      val scope = scopesWorklist.remove()
      // 1. get all descriptors
      val descriptors = scope.getContributedDescriptors { true }
      // 2. add the interesting ones to the result
      result.addAll(descriptors.filter(predicate))
      // 3. add all new member scopes to the worklist
      scopesWorklist.addAll(
        descriptors.filterIsInstance<ClassDescriptor>().map { it.completeUnsubstitutedScope }
      )
      scopesWorklist.addAll(
        descriptors.filterIsInstance<TypeAliasDescriptor>().mapNotNull {
          it.classDescriptor?.completeUnsubstitutedScope
        }
      )
    } else if (packagesWorklist.isNotEmpty()) {
      // work to do in a package
      val pkg: FqName = packagesWorklist.remove()
      // 1. add the scope to the worklist
      getPackage(pkg)?.memberScope?.let { scopesWorklist.add(it) }
      // 2. add the subpackages to the worklist
      if (addSubPackages) packagesWorklist.addAll(getSubPackagesOf(pkg) { true })
    } else {
      break
    }
  }

  return result.toList()
}

val ClassDescriptor.completeUnsubstitutedScope: MemberScope
  get() =
    ChainedMemberScope.create(
      debugName = "ChainMemberScope",
      scopes = listOf(unsubstitutedMemberScope, staticScope, unsubstitutedInnerClassesScope),
    )
