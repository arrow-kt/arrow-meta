package arrow.meta.quotes

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeAlias

fun List<DeclarationDescriptor>.namedFunctionDescriptor(element: KtNamedFunction): FunctionDescriptor? = this.firstOrNull { (it as? FunctionDescriptor)?.let {
  it.name == element.nameAsSafeName
  && it.valueParameters.zip(element.valueParameters) { descriptor, element -> descriptor.type.toString() == element.typeReference?.text }.none { !it }
} == true } as? FunctionDescriptor

fun List<DeclarationDescriptor>.propertyDescriptor(element: KtProperty): PropertyDescriptor? = this.firstOrNull { (it as? PropertyDescriptor)?.let {
  it.name == element.nameAsName && it.type.toString() == element.typeReference?.text
} == true  } as? PropertyDescriptor

fun List<DeclarationDescriptor>.typeAliasDescriptor(element: KtTypeAlias): TypeAliasDescriptor? = this.firstOrNull { (it as? TypeAliasDescriptor)?.let {
  it.name == element.nameAsName
} == true } as? TypeAliasDescriptor