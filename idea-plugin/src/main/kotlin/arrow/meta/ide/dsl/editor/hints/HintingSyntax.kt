package arrow.meta.ide.dsl.editor.hints

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.lang.ExpressionTypeProvider
import com.intellij.lang.LanguageExpressionTypes
import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.LanguageParameterInfo
import com.intellij.lang.parameterInfo.ParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoHandler
import com.intellij.lang.parameterInfo.ParameterInfoHandlerWithTabActionSupport
import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.idea.parameterInfo.KotlinParameterInfoWithCallHandlerBase
import org.jetbrains.kotlin.idea.parameterInfo.KotlinTypeArgumentInfoHandlerBase
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import kotlin.reflect.KClass

/**
 * Hint's are generally used with TypeInferenceAlgorithms.
 * [HintingSyntax] provides means to connect those algorithms to the editor for specified element's.
 */
interface HintingSyntax {
  /**
   * registers a [ExpressionTypeProvider] for [KtExpression]s.
   * Use this function to add an improved TypeInferenceAlgorithm for Kotlin to the ide.
   * @param informationHint render's the Type of the  evaluated expression
   * @param expressionAt provides all [KtExpression] from a [PsiElement]
   * @param errorHint if the Type can't be detected
   * @sample [org.jetbrains.kotlin.idea.codeInsight.KotlinExpressionTypeProvider]
   */
  fun IdeMetaPlugin.addExpressionTypeProviderForKotlin(
    informationHint: (expression: KtExpression) -> String,
    expressionAt: (elementAt: PsiElement) -> MutableList<KtExpression>,
    errorHint: String = "No expression Found",
    hasAdvancedInformation: Boolean = false,
    advancedInformation: (expression: KtExpression) -> String = Noop.string1()
  ): ExtensionPhase =
    addExpressionTypeProvider(informationHint, expressionAt, errorHint, hasAdvancedInformation, advancedInformation)

  /**
   * registers a [ExpressionTypeProvider] for an expressionType [A] in a language.
   * This extension can be used to bring TypeInferenceAlgorithms to the ide and is a language independent version of [addExpressionTypeProviderForKotlin].
   * @see addExpressionTypeProviderForKotlin
   */
  fun <A : PsiElement> IdeMetaPlugin.addExpressionTypeProvider(
    informationHint: (expression: A) -> String,
    expressionAt: (elementAt: PsiElement) -> MutableList<A>,
    errorHint: String = "No expression Found",
    hasAdvancedInformation: Boolean = false,
    advancedInformation: (expression: A) -> String = Noop.string1()
  ): ExtensionPhase =
    extensionProvider(
      LanguageExpressionTypes.INSTANCE,
      object : ExpressionTypeProvider<A>() {
        override fun getInformationHint(element: A): String = informationHint(element)
        override fun hasAdvancedInformation(): Boolean = hasAdvancedInformation
        override fun getExpressionsAt(elementAt: PsiElement): MutableList<A> = expressionAt(elementAt)
        override fun getErrorHint(): String = errorHint
        override fun getAdvancedInformationHint(element: A): String = advancedInformation(element)
      }
    )

  /**
   * @param findElementForUpdatingParameterInfo returning null removes hint
   */
  fun <Owner : PsiElement, Type> IdeMetaPlugin.addParameterInfoHandler(
    showParameterInfo: (element: Owner, context: CreateParameterInfoContext) -> Unit,
    updateParameterInfo: (parameterOwner: Owner, context: UpdateParameterInfoContext) -> Unit,
    updateUI: (p: Type, context: ParameterInfoUIContext) -> Unit = Noop.effect2,
    parameterForLookUp: (item: LookupElement?, context: ParameterInfoContext?) -> Array<Any>? = Noop.nullable2(),
    couldShowInLookup: Boolean = false,
    findElementForUpdatingParameterInfo: (context: UpdateParameterInfoContext) -> Owner? = Noop.nullable1(),
    findElementForParameterInfo: (context: CreateParameterInfoContext) -> Owner? = Noop.nullable1()
  ): ExtensionPhase =
    extensionProvider(
      LanguageParameterInfo.INSTANCE,
      object : ParameterInfoHandler<Owner, Type> {
        override fun showParameterInfo(element: Owner, context: CreateParameterInfoContext) =
          showParameterInfo(element, context)

        override fun updateParameterInfo(parameterOwner: Owner, context: UpdateParameterInfoContext) =
          updateParameterInfo(parameterOwner, context)

        override fun updateUI(p: Type, context: ParameterInfoUIContext) =
          updateUI(p, context)

        override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? =
          parameterForLookUp(item, context)

        override fun couldShowInLookup(): Boolean = couldShowInLookup

        override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): Owner? =
          findElementForUpdatingParameterInfo(context)

        override fun findElementForParameterInfo(context: CreateParameterInfoContext): Owner? =
          findElementForParameterInfo(context)
      }
    )

  /**
   * {Type being an Descriptor check Subtypes of [org.jetbrains.kotlin.idea.parameterInfo.KotlinTypeArgumentInfoHandlerBase]
   * or [org.jetbrains.kotlin.idea.parameterInfo.KotlinParameterInfoWithCallHandlerBase]
   */
  fun <Owner : PsiElement, Type, ActualType : PsiElement> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    actualParameterDelimiterType: IElementType,
    actualParametersRBraceType: IElementType,
    showParameterInfo: (element: Owner, context: CreateParameterInfoContext) -> Unit,
    argListStopSearchClasses: MutableSet<out Class<Any>>,
    updateParameterInfo: (parameterOwner: Owner, context: UpdateParameterInfoContext) -> Unit,
    updateUI: (p: Type, context: ParameterInfoUIContext) -> Unit,
    parametersForLookup: (item: LookupElement?, context: ParameterInfoContext?) -> Array<Any>?,
    actualParameters: (o: Owner) -> Array<ActualType>,
    couldShowInLookup: Boolean,
    argumentListClass: Class<Owner>,
    argumentListAllowedParentClasses: MutableSet<Class<Any>>,
    findElementForUpdatingParameterInfo: (context: UpdateParameterInfoContext) -> Owner?,
    findElementForParameterInfo: (context: CreateParameterInfoContext) -> Owner?
  ): ExtensionPhase =
    extensionProvider(
      LanguageParameterInfo.INSTANCE,
      object : ParameterInfoHandlerWithTabActionSupport<Owner, Type, ActualType> {
        override fun getActualParameterDelimiterType(): IElementType =
          actualParameterDelimiterType

        override fun getActualParametersRBraceType(): IElementType =
          actualParametersRBraceType

        override fun showParameterInfo(element: Owner, context: CreateParameterInfoContext) =
          showParameterInfo(element, context)

        override fun getArgListStopSearchClasses(): MutableSet<out Class<Any>> =
          argListStopSearchClasses

        override fun updateParameterInfo(parameterOwner: Owner, context: UpdateParameterInfoContext) =
          updateParameterInfo(parameterOwner, context)

        override fun updateUI(p: Type, context: ParameterInfoUIContext) =
          updateUI(p, context)

        override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? =
          parametersForLookup(item, context)

        override fun getActualParameters(o: Owner): Array<ActualType> =
          actualParameters(o)

        override fun couldShowInLookup(): Boolean = couldShowInLookup

        override fun getArgumentListClass(): Class<Owner> = argumentListClass

        override fun getArgumentListAllowedParentClasses(): MutableSet<Class<Any>> =
          argumentListAllowedParentClasses

        override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): Owner? =
          findElementForUpdatingParameterInfo(context)

        override fun findElementForParameterInfo(context: CreateParameterInfoContext): Owner? =
          findElementForParameterInfo(context)
      }
    )

  /**
   * This is used for [DeclarationDescriptor]'s
   * @see addParameterInfoHandlerForKotlin
   */
  fun <Type : DeclarationDescriptor> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    fetchTypeParameters: (descriptor: Type) -> List<TypeParameterDescriptor>,
    findParameterOwners: (argumentList: KtTypeArgumentList) -> Collection<Type>?,
    argumentListAllowedParentClasses: MutableSet<Class<Any>>
  ): ExtensionPhase =
    extensionProvider(
      LanguageParameterInfo.INSTANCE,
      object : KotlinTypeArgumentInfoHandlerBase<Type>() {
        override fun fetchTypeParameters(descriptor: Type): List<TypeParameterDescriptor> =
          fetchTypeParameters(descriptor)

        override fun findParameterOwners(argumentList: KtTypeArgumentList): Collection<Type>? =
          findParameterOwners(argumentList)

        override fun getArgumentListAllowedParentClasses(): MutableSet<Class<Any>> =
          argumentListAllowedParentClasses
      }
    )

  /**
   * This is used for [FunctionDescriptor]'s
   * @see addParameterInfoHandlerForKotlin
   */
  @Suppress
  fun <ArgumentList : KtElement, Argument : KtElement> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    argumentList: KClass<ArgumentList>,
    argument: KClass<Argument>,
    actualParameters: (o: ArgumentList) -> Array<Argument>,
    actualParametersRBraceType: IElementType,
    argumentListAllowedParentClasses: MutableSet<Class<Any>>
  ): ExtensionPhase =
    extensionProvider(
      LanguageParameterInfo.INSTANCE,
      object : KotlinParameterInfoWithCallHandlerBase<ArgumentList, Argument>(argumentList, argument) {
        override fun getActualParameters(o: ArgumentList): Array<Argument> =
          actualParameters(o)

        override fun getActualParametersRBraceType(): IElementType =
          actualParametersRBraceType

        override fun getArgumentListAllowedParentClasses(): MutableSet<Class<Any>> =
          argumentListAllowedParentClasses
      }
    )
}
