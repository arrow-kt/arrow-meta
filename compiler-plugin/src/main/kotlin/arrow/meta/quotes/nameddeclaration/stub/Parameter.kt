package arrow.meta.quotes.nameddeclaration.stub

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.declaration.DestructuringDeclaration
import arrow.meta.quotes.expression.loopexpression.ForExpression
import arrow.meta.quotes.modifierlistowner.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter

/**
 * <code>""" $name : $type""".classParameter</code>
 *
 * A template destructuring [Scope] for a [KtParameter]
 *
 * ### Parameter Formatting:
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.parameter
 *
 * val Meta.reformatParameter: Plugin
 *  get() =
 *   "ReformatParameter" {
 *    meta(
 *     parameter({ true }) { param ->
 *      Transform.replace(
 *       replacing = param,
 *       newDeclaration = " $name: $type = EnvironmentRepository()".classParameter
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 *
 * ### Class Parameter:
 * A loop parameter may be found within a [ClassDeclaration]. For example, we can change:
 * ```
 * class A(val environmentRepository: Repository)
 * ```
 * to:
 * ```
 * class A(val environmentRepository: Repository = EnvironmentRepository())
 * ```
 * By working with loop parameters:
 *  ```
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.parameter
 *
 * val Meta.assignEnvironmentRepositoryConstructorParameterADefaultValue: Plugin
 *  get() =
 *   "Make all environment constructor parameters open" {
 *    meta(
 *     parameter({ name == "environmentRepository" }) { param ->
 *      Transform.replace(
 *       replacing = param,
 *       newDeclaration = " $name: $type = EnvironmentRepository()".classParameter
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 * ### Loop Parameter:
 *  A loop parameter may be found within a [ForExpression]. For example, we can change
 * ```
 * for (i in list) { ... }
 * ```
 * to:
 * ```
 * for (row in list) { ... }
 * ```
 * By working with loop parameters:
 * ```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.parameter
 *
 * val Meta.renameLoopParameter: Plugin
 *  get() =
 *   "RenameLoopParameter" {
 *    meta(
 *     parameter({ name == "i" }) { param ->
 *      Transform.replace(
 *       replacing = param,
 *       newDeclaration = "row".loopParameter
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 * ### Destructuring Parameter:
 * A destructuring parameter may be found within a [FunctionLiteral]. For example, we can change:
 * ```
 * someFunction(x) { func -> ... }
 * ```
 * to:
 * ```
 * someFunction(x) { function -> ... }
 * ```
 * By working with destructuring parameters:
 *```kotlin:ank:silent
 * import arrow.meta.Meta
 * import arrow.meta.Plugin
 * import arrow.meta.invoke
 * import arrow.meta.quotes.Transform
 * import arrow.meta.quotes.parameter
 *
 * val Meta.renameDestructuringParameter: Plugin
 *  get() =
 *   "RenameDestructuringParameter" {
 *    meta(
 *      parameter({ typeReference?.name == "func" }) { param ->
 *      Transform.replace(
 *       replacing = param,
 *       newDeclaration = "function".destructuringDeclaration
 *      )
 *      }
 *     )
 *    }
 * ```
 */

class Parameter(
  override val value: KtParameter?,
  val name: Name? = value?.nameAsName,
  val type: TypeReference = TypeReference(value?.typeReference),
  val `(typeParams)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value?.typeParameters
    ?: listOf(), postfix = ">"),
  val defaultValue: Scope<KtExpression> = Scope(value?.defaultValue),
  val valOrVar: Name = when {
    value?.hasValOrVar() == true && value.isMutable -> "var"
    value?.hasValOrVar() == true && !value.isMutable -> "val"
    value?.isVarArg == true -> "vararg"
    else -> ""
  }.let(Name::identifier),
  val destructuringDeclaration: DestructuringDeclaration = DestructuringDeclaration(value?.destructuringDeclaration)
) : Scope<KtParameter>(value)