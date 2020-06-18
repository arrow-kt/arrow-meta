package arrow.meta.quotes

import org.jetbrains.kotlin.codegen.coroutines.createCustomCopy
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.TypeAliasDescriptor
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTypeAlias

interface DescriptorQuote<D : DeclarationDescriptor> {

  fun asDescriptor(): D?
}

private fun <D : DeclarationDescriptor> descriptorQuote(transform: () -> D?): DescriptorQuote<D> = object : DescriptorQuote<D> {
  override fun asDescriptor(): D? = transform()
}

fun List<DeclarationDescriptor>.namedFunctionDescriptor(element: KtNamedFunction): FunctionDescriptor? = descriptorQuote {
  this.firstOrNull { (it as? FunctionDescriptor)?.let {
    it.isOperator
    it.name == element.nameAsSafeName
    && it.returnType?.toString() == element.typeReference?.text
    && it.valueParameters.zip(element.valueParameters) { descriptor, element -> descriptor.type.toString() == element.typeReference?.text }.none { !it }
  } == true } as? FunctionDescriptor
}.asDescriptor()

fun List<DeclarationDescriptor>.propertyDescriptor(element: KtProperty): PropertyDescriptor? = descriptorQuote {
  this.firstOrNull { (it as? PropertyDescriptor)?.let {
    it.name == element.nameAsName && it.type.toString() == element.typeReference?.text
  } == true  } as? PropertyDescriptor
}.asDescriptor()

fun List<DeclarationDescriptor>.typeAliasDescriptor(element: KtTypeAlias): TypeAliasDescriptor? = descriptorQuote {
  this.firstOrNull { (it as? TypeAliasDescriptor)?.let {
    it.name == element.nameAsName
  } == true } as? TypeAliasDescriptor
}.asDescriptor()