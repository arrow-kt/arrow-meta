package arrow.meta.quotes.nameddeclaration.stub

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.expression.loopexpression.ForLoopExpression
import arrow.meta.quotes.modifierlist.TypeReference
import arrow.meta.quotes.nameddeclaration.notstubbed.FunctionLiteral
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.psiUtil.modalityModifierType
import org.jetbrains.kotlin.psi.psiUtil.visibilityModifierType

/**
 * <code>"""$visibility $name : $type""".classParameter</code>
 *
 * A template destructuring [Scope] for a [KtParameter]
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
 *  ```kotlin:ank:silent
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
 *       newDeclaration = "$visibility $name: $type = EnvironmentRepository()".classParameter
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 * ### Loop Parameter:
 *  A loop parameter may be found within a [ForLoopExpression]. For example, we can change
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
 *     parameter({ name?.type?.name == "func" }) { param ->
 *      Transform.replace(
 *       replacing = param,
 *       newDeclaration = "function".destructuringDeclaration
 *      )
 *      }
 *     )
 *    }
 * ```
 *
 */

class Parameter(
  override val value: KtParameter?,
  val modality: Name? = value?.modalityModifierType()?.value?.let(Name::identifier),
  val visibility: Name? = value?.visibilityModifierType()?.value?.let(Name::identifier),
  val name: Name? = value?.nameAsName,
  val `@annotations`: ScopedList<KtAnnotationEntry> = ScopedList(value?.annotationEntries
    ?: listOf()),
  val type: TypeReference = TypeReference(value?.typeReference),
  val `(typeParams)`: ScopedList<KtTypeParameter> = ScopedList(prefix = "<", value = value?.typeParameters
    ?: listOf(), postfix = ">"),
  val defaultValue: Scope<KtExpression> = Scope(value?.defaultValue)
) : Scope<KtParameter>(value)