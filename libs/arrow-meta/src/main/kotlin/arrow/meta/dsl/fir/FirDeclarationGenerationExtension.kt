package arrow.meta.dsl.fir

import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.symbols.impl.FirClassLikeSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

fun FirContext.declarationGeneration(
  generateClassLikeDeclaration: ((classId: ClassId) -> FirClassLikeSymbol<*>?)? = null,
  generateConstructors: ((owner: FirClassSymbol<*>) -> List<FirConstructorSymbol>)? = null,
  generateFunctions:
    ((callableId: CallableId, owner: FirClassSymbol<*>?) -> List<FirNamedFunctionSymbol>)? =
    null,
  generateProperties:
    ((callableId: CallableId, owner: FirClassSymbol<*>?) -> List<FirPropertySymbol>)? =
    null,
  callableNamesForClass: ((classSymbol: FirClassSymbol<*>) -> Set<Name>)? = null,
  nestedClassifiersNames: ((classSymbol: FirClassSymbol<*>) -> Set<Name>)? = null,
  topLevelCallableIds: (() -> Set<CallableId>)? = null,
  topLevelClassIds: (() -> Set<ClassId>)? = null,
  hasPackage: ((packageFqName: FqName) -> Boolean)? = null,
): FirDeclarationGenerationExtension =
  object : FirDeclarationGenerationExtension(firSession) {
    override fun generateClassLikeDeclaration(classId: ClassId): FirClassLikeSymbol<*>? =
      if (generateClassLikeDeclaration == null) super.generateClassLikeDeclaration(classId)
      else generateClassLikeDeclaration(classId)

    override fun generateConstructors(owner: FirClassSymbol<*>): List<FirConstructorSymbol> =
      if (generateConstructors == null) super.generateConstructors(owner)
      else generateConstructors(owner)

    override fun generateFunctions(
      callableId: CallableId,
      owner: FirClassSymbol<*>?
    ): List<FirNamedFunctionSymbol> =
      if (generateFunctions == null) super.generateFunctions(callableId, owner)
      else generateFunctions(callableId, owner)

    override fun generateProperties(
      callableId: CallableId,
      owner: FirClassSymbol<*>?
    ): List<FirPropertySymbol> =
      if (generateProperties == null) super.generateProperties(callableId, owner)
      else generateProperties(callableId, owner)

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>): Set<Name> =
      if (callableNamesForClass == null) super.getCallableNamesForClass(classSymbol)
      else callableNamesForClass(classSymbol)

    override fun getNestedClassifiersNames(classSymbol: FirClassSymbol<*>): Set<Name> =
      if (nestedClassifiersNames == null) super.getNestedClassifiersNames(classSymbol)
      else nestedClassifiersNames(classSymbol)

    override fun getTopLevelCallableIds(): Set<CallableId> =
      if (topLevelCallableIds == null) super.getTopLevelCallableIds() else topLevelCallableIds()

    override fun getTopLevelClassIds(): Set<ClassId> =
      if (topLevelClassIds == null) super.getTopLevelClassIds() else topLevelClassIds()

    override fun hasPackage(packageFqName: FqName): Boolean =
      if (hasPackage == null) super.hasPackage(packageFqName) else hasPackage(packageFqName)
  }
