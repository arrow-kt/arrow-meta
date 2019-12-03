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
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.idea.parameterInfo.KotlinFunctionParameterInfoHandler
import org.jetbrains.kotlin.idea.parameterInfo.KotlinLambdaParameterInfoHandler
import org.jetbrains.kotlin.idea.parameterInfo.KotlinParameterInfoWithCallHandlerBase
import org.jetbrains.kotlin.idea.parameterInfo.KotlinTypeArgumentInfoHandlerBase
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.psi.KtTypeProjection
import kotlin.reflect.KClass

/**
 * Hint's are generally used with TypeInferenceAlgorithms.
 * [HintingSyntax] provides means to connect those algorithms to the ide for specified element's.
 */
interface HintingSyntax {
  /**
   * registers a [ExpressionTypeProvider] for [KtExpression]s.
   * Use this function to add an improved TypeInferenceAlgorithm for Kotlin to the ide.
   * [informationHint] and [expressionAt] will be executed as the user types.
   * @param informationHint resolves and render's the Type of the evaluated expression
   * @param expressionAt provides all [KtExpression] from a [PsiElement]
   * @param errorHint if the Type can't be detected
   * @sample [org.jetbrains.kotlin.idea.codeInsight.KotlinExpressionTypeProvider]
   */
  fun IdeMetaPlugin.addExpressionTypeProviderForKotlin(
    informationHint: BindingContext.(expression: KtExpression) -> String,
    expressionAt: (elementAt: PsiElement) -> MutableList<KtExpression>,
    errorHint: String = "No expression Found",
    hasAdvancedInformation: Boolean = false,
    advancedInformation: (expression: KtExpression) -> String = Noop.string1()
  ): ExtensionPhase =
    addExpressionTypeProvider({ informationHint(it.analyze(BodyResolveMode.PARTIAL), it) }, expressionAt, errorHint, hasAdvancedInformation, advancedInformation)

  /**
   * registers an [ExpressionTypeProvider] for an expressionType [A] in any given language.
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
   * registers a [ParameterInfoHandler]
   * The latter is generally used to render properties and types of parameters for code insight.
   * @sample [KotlinLambdaParameterInfoHandler], [KotlinFunctionParameterInfoHandler]
   * @param Type is a Descriptor
   * @param Owner is the list of Parameters in the Descriptor [Type]
   * @see addParameterInfoHandlerForKotlin
   */
  fun <Owner : PsiElement, Type> IdeMetaPlugin.addParameterInfoHandler(handler: ParameterInfoHandler<Owner, Type>): ExtensionPhase =
    extensionProvider(LanguageParameterInfo.INSTANCE, handler)

  /**
   * @param findElementForUpdatingParameterInfo returning null removes hint
   * @see addParameterInfoHandler
   */
  fun <Owner : PsiElement, Type> IdeMetaPlugin.parameterInfoHandler(
    showParameterInfo: (element: Owner, context: CreateParameterInfoContext) -> Unit,
    updateParameterInfo: (parameterOwner: Owner, context: UpdateParameterInfoContext) -> Unit,
    updateUI: (p: Type, context: ParameterInfoUIContext) -> Unit = Noop.effect2,
    parameterForLookUp: (item: LookupElement?, context: ParameterInfoContext?) -> Array<Any>? = Noop.nullable2(),
    couldShowInLookup: Boolean = false,
    findElementForUpdatingParameterInfo: (context: UpdateParameterInfoContext) -> Owner? = Noop.nullable1(),
    findElementForParameterInfo: (context: CreateParameterInfoContext) -> Owner? = Noop.nullable1(),
    syncUpdateOnCaretMove: (context: UpdateParameterInfoContext) -> Unit = Noop.effect1,
    isWhiteSpaceSensitive: Boolean = false
  ): ParameterInfoHandler<Owner, Type> =
    object : ParameterInfoHandler<Owner, Type> {
      override fun showParameterInfo(element: Owner, context: CreateParameterInfoContext): Unit =
        showParameterInfo(element, context)

      override fun updateParameterInfo(parameterOwner: Owner, context: UpdateParameterInfoContext): Unit =
        updateParameterInfo(parameterOwner, context)

      override fun updateUI(p: Type, context: ParameterInfoUIContext): Unit = updateUI(p, context)

      override fun getParametersForLookup(item: LookupElement?, context: ParameterInfoContext?): Array<Any>? =
        parameterForLookUp(item, context)

      override fun couldShowInLookup(): Boolean = couldShowInLookup

      override fun findElementForUpdatingParameterInfo(context: UpdateParameterInfoContext): Owner? =
        findElementForUpdatingParameterInfo(context)

      override fun findElementForParameterInfo(context: CreateParameterInfoContext): Owner? =
        findElementForParameterInfo(context)

      override fun syncUpdateOnCaretMove(context: UpdateParameterInfoContext): Unit =
        syncUpdateOnCaretMove(context)

      override fun isWhitespaceSensitive(): Boolean = isWhiteSpaceSensitive
    }

  /**
   * registers a [KotlinTypeArgumentInfoHandlerBase]
   * This extension is used for [DeclarationDescriptor]'s, the `Owner` is [KtTypeArgumentList] and the `ActualType` is [KtTypeProjection].
   * @see addParameterInfoHandlerForKotlin
   */
  fun <Type : DeclarationDescriptor> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    fetchTypeParameters: (descriptor: Type) -> List<TypeParameterDescriptor>,
    findParameterOwners: (argumentList: KtTypeArgumentList) -> Collection<Type>?,
    argumentListAllowedParentClasses: MutableSet<Class<Any>>
  ): ExtensionPhase =
    addParameterInfoHandler(
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
   * registers a [KotlinParameterInfoWithCallHandlerBase]
   * This is used for [FunctionDescriptor]'s, the `Owner` is [ArgumentList] and the `ActualType` is [Argument]. Naturally, `Type` is a [FunctionDescriptor].
   * @see addParameterInfoHandlerForKotlin
   * @see KotlinParameterInfoWithCallHandlerBase and its SubType.
   */
  @Suppress
  fun <ArgumentList : KtElement, Argument : KtElement> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    argumentList: KClass<ArgumentList>,
    argument: KClass<Argument>,
    actualParameters: (o: ArgumentList) -> Array<Argument>,
    actualParametersRBraceType: IElementType,
    argumentListAllowedParentClasses: MutableSet<Class<Any>>
  ): ExtensionPhase =
    addParameterInfoHandler(
      object : KotlinParameterInfoWithCallHandlerBase<ArgumentList, Argument>(argumentList, argument) {
        override fun getActualParameters(o: ArgumentList): Array<Argument> =
          actualParameters(o)

        override fun getActualParametersRBraceType(): IElementType =
          actualParametersRBraceType

        override fun getArgumentListAllowedParentClasses(): MutableSet<Class<Any>> =
          argumentListAllowedParentClasses
      }
    )

  /**
   * registers a [ParameterInfoHandlerWithTabActionSupport].
   * One among other goal's in this extension, is to facilitate a Mapping between [Owner] -> [ActualType], or in other words from `List<ActualType>` -> `ActualType`.
   * This is evident in [actualParameters]. Check out [addParameterInfoHandlerForKotlin] for a minimal example to what this extension may abstract to.
   * The difference between [addParameterInfoHandlerForKotlin] and this function is that the latter is language independent.
   * @see addParameterInfoHandler
   * @param ActualType is one parameter of the collection in [Owner]
   * @param Type being an Descriptor
   * @see [org.jetbrains.kotlin.idea.parameterInfo.KotlinTypeArgumentInfoHandlerBase] and its SubTypes.
   * @see [org.jetbrains.kotlin.idea.parameterInfo.KotlinParameterInfoWithCallHandlerBase] and its SubTypes.
   */
  fun <Owner : PsiElement, Type, ActualType : PsiElement> IdeMetaPlugin.addParameterInfoHandler(
    actualParameters: (o: Owner) -> Array<ActualType>,
    argumentListClass: Class<Owner>,
    actualParameterDelimiterType: IElementType,
    actualParametersRBraceType: IElementType,
    argumentListAllowedParentClasses: MutableSet<Class<Any>>,
    argListStopSearchClasses: MutableSet<out Class<Any>>,
    showParameterInfo: (element: Owner, context: CreateParameterInfoContext) -> Unit,
    updateParameterInfo: (parameterOwner: Owner, context: UpdateParameterInfoContext) -> Unit,
    updateUI: (p: Type, context: ParameterInfoUIContext) -> Unit,
    parametersForLookup: (item: LookupElement?, context: ParameterInfoContext?) -> Array<Any>?,
    couldShowInLookup: Boolean,
    findElementForUpdatingParameterInfo: (context: UpdateParameterInfoContext) -> Owner?,
    findElementForParameterInfo: (context: CreateParameterInfoContext) -> Owner?,
    syncUpdateOnCaretMove: (context: UpdateParameterInfoContext) -> Unit = Noop.effect1,
    isWhiteSpaceSensitive: Boolean = actualParameterDelimiterType == TokenType.WHITE_SPACE
  ): ExtensionPhase =
    addParameterInfoHandler(
      object : ParameterInfoHandlerWithTabActionSupport<Owner, Type, ActualType> {
        override fun getActualParameterDelimiterType(): IElementType =
          actualParameterDelimiterType

        override fun getActualParametersRBraceType(): IElementType =
          actualParametersRBraceType

        override fun showParameterInfo(element: Owner, context: CreateParameterInfoContext): Unit =
          showParameterInfo(element, context)

        override fun getArgListStopSearchClasses(): MutableSet<out Class<Any>> =
          argListStopSearchClasses

        override fun updateParameterInfo(parameterOwner: Owner, context: UpdateParameterInfoContext): Unit =
          updateParameterInfo(parameterOwner, context)

        override fun updateUI(p: Type, context: ParameterInfoUIContext): Unit = updateUI(p, context)

        override fun syncUpdateOnCaretMove(context: UpdateParameterInfoContext): Unit =
          syncUpdateOnCaretMove(context)

        override fun isWhitespaceSensitive(): Boolean = isWhiteSpaceSensitive

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
}
