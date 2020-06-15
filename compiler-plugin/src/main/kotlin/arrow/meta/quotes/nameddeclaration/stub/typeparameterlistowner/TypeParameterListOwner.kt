package arrow.meta.quotes.nameddeclaration.stub.typeparameterlistowner

import arrow.meta.quotes.Scope
import arrow.meta.quotes.ScopedList
import arrow.meta.quotes.TypedScope
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.psi.KtExpressionWithLabel
import org.jetbrains.kotlin.psi.KtTypeConstraint
import org.jetbrains.kotlin.psi.KtTypeParameter
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner

/**
 * A template destructuring [Scope] for a [KtExpressionWithLabel]
 */
open class TypeParameterListOwner<out T: KtTypeParameterListOwner, out D: DeclarationDescriptor>(
  override val value: T,
  override val typeInformation: D,
  open val `(typeConstraints)`: ScopedList<KtTypeConstraint> = ScopedList(value = value.typeConstraints),
  open val `(typeParams)`: ScopedList<KtTypeParameter> = ScopedList(value = value.typeParameters)
) : TypedScope<T, D>(value, typeInformation)