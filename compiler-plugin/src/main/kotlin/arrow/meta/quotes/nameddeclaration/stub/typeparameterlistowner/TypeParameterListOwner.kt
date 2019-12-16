package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtTypeConstraint
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner

/**
 * A template destructuring [Scope] for a [KtExpressionWithLabel]
 */
open class TypeParameterListOwner<out T: KtTypeParameterListOwner>(
  override val value: T,
  open val `(typeConstraints)`: ScopedList<KtTypeConstraint> = ScopedList(value = value.typeConstraints),
  open val `(typeParams)`: ScopedList<KtTypeParameter> = ScopedList(value = value.typeParameters)
) : Scope<T>(value)