package arrow.meta.dsl.fir.ast

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirProperty
import kotlin.reflect.KClass

data class NameFilter<D : FirElement>(val node: Element<D>, val declarationClass: KClass<D>, val name: String) {
  companion object {
    inline operator fun <reified D : FirElement> invoke(node: Element<D>, name: String): NameFilter<D> =
      NameFilter(node, D::class, name)
  }
}

sealed interface Element<D : FirElement> {
  operator fun get(name: String): NameFilter<D>
}

object Files : Element<FirFile> {
  override operator fun get(name: String): NameFilter<FirFile> =
    NameFilter(this, name)

  val classes: Classes = Classes(this)
  val properties: Properties = Properties(this)
  val functions: Functions = Functions(this)
}

data class Classes(val parent: Element<*>) : Element<FirClass> {

  val classes: Classes = Classes(this)
  val properties: Properties = Properties(this)
  val functions: Functions = Functions(this)

  override operator fun get(name: String): NameFilter<FirClass> =
    NameFilter(this, name)
}

data class Properties(val parent: Element<*>) : Element<FirProperty> {
  override operator fun get(name: String): NameFilter<FirProperty> =
    NameFilter(this, name)
}

data class Functions(val parent: Element<*>) : Element<FirFunction> {

  override operator fun get(name: String): NameFilter<FirFunction> =
    NameFilter(this, name)
}

val flow =
  Files.classes.properties["x"]


/**
 * - files
 *  - declarations
 *    - classes
 *      - initializer
 *      - supertypes
 *      - name
 *      - constructors
 *      - properties
 *      - inner classes
 *      - functions
 *    - objects
 *      - initializer
 *      - supertypes
 *      - name
 *      - properties
 *      - inner classes
 *      - functions
 *    - interfaces
 *      - supertypes
 *      - name
 *      - properties
 *      - inner classes
 *      - functions
 *    - functions
 *      - typeArguments
 *      - receivers
 *      - name
 *      - value arguments
 *      - returnType
 *      - body
 *        - statements
 *        - expressions
 *    - properties (fields, getter, setter)
 *      - typeArguments
 *      - receivers
 *      - name
 *      - returnType
 *      - body
 *        - statements
 *        - expressions
 *  -
 */
