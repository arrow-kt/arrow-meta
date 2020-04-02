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
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.lazy.BodyResolveMode
import org.jetbrains.kotlin.psi.KtTypeProjection
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import org.jetbrains.kotlin.psi.psiUtil.startOffset
import kotlin.reflect.KClass
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.renderer.ClassifierNamePolicy
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtTryExpression
import org.jetbrains.kotlin.psi.KtWhenExpression

/**
 * Hint's are generally used with TypeInferenceAlgorithms.
 * [HintingSyntax] provides means to connect those algorithms to the ide for specified element's.
 */
interface HintingSyntax {
  //TODO: consider if PsiElement.selectionTextRangeOnTextEditor from the following example is universal

  /**
   * registers a [ExpressionTypeProvider] for [KtExpression]s.
   * Use this function to add an improved TypeInferenceAlgorithm for Kotlin to the ide.
   * [informationHint] and [expressionAt] will be executed as the user types.
   * The following examples is a minimal version of the
   * [org.jetbrains.kotlin.idea.codeInsight.KotlinExpressionTypeProvider] and targets [KtFunction]s, who are expressed as expressions.
   *
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import com.intellij.openapi.editor.ex.util.EditorUtil
   * import com.intellij.openapi.fileEditor.FileEditorManager
   * import com.intellij.openapi.fileEditor.TextEditor
   * import com.intellij.openapi.util.TextRange
   * import com.intellij.openapi.vfs.VirtualFile
   * import com.intellij.psi.PsiElement
   * import org.jetbrains.kotlin.descriptors.CallableDescriptor
   * import org.jetbrains.kotlin.psi.KtFunction
   * import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf
   * import org.jetbrains.kotlin.renderer.DescriptorRenderer
   * import org.jetbrains.kotlin.renderer.RenderingFormat
   * import org.jetbrains.kotlin.resolve.BindingContext
   * import org.jetbrains.kotlin.types.KotlinType
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * //sampleStart
   * val IdeMetaPlugin.expressionHints: Plugin
   *  get() = "Hints for KtExpressions" {
   *   meta(
   *    addExpressionTypeProviderForKotlin(
   *     informationHint = { expr ->
   *      get(BindingContext.DECLARATION_TO_DESCRIPTOR, expr).safeAs<CallableDescriptor>()?.returnType
   *       ?.let { result: KotlinType ->
   *         typeRenderer.renderType(result)
   *        } ?: "Type is unknown"
   *    },
   *    expressionAt = {
   *      it.selectionTextRangeOnTextEditor().let { range: TextRange ->
   *        if (!range.isEmpty)
   *          it.parentsWithSelf.filterIsInstance<KtFunction>()
   *            .filter { f: KtFunction -> !f.hasBlockBody() && !f.hasDeclaredReturnType() }
   *            .run {
   *              val element: KtFunction? = firstOrNull { f: KtFunction -> f.textRange.contains(range) }
   *              filter { candidate: KtFunction ->
   *                candidate.textRange?.startOffset == element?.textRange?.startOffset
   *              }.toList() // you may noticed that this list has maximum one member
   *            }
   *        else emptyList()
   *      }
   *     }
   *    )
   *   )
   *  }
   * //sampleEnd
   *
   * private val IdeMetaPlugin.typeRenderer: DescriptorRenderer
   *  get() = DescriptorRenderer.COMPACT_WITH_SHORT_TYPES.withOptions {
   *    textFormat = RenderingFormat.HTML
   *    classifierNamePolicy = classifierNamePolicy()
   *  }
   *
   * /**
   * * evaluates the SelectionTextRange of [PsiElement]'s `containedFile`,
   * * when the current Editor is a [TextEditor].
   * */
   * private fun PsiElement.selectionTextRangeOnTextEditor(): TextRange =
   *  containingFile?.virtualFile?.let { file: VirtualFile ->
   *   FileEditorManager.getInstance(project)
   *     .getSelectedEditor(file)?.safeAs<TextEditor>()?.run { EditorUtil.getSelectionInAnyMode(editor) }
   *  } ?: TextRange.EMPTY_RANGE
   * ```
   * The aforementioned example [informationHint] is targeting [CallableDescriptor]s, which is sufficient for [FunctionDescriptor]s - the latter
   * is isomorphic to the PsiElement representation [KtFunction], which has a lot less information than the `Descriptor`.
   * In fact Kotlin describes these mechanism for specific [KtProperty]s, [KtDestructuringDeclarationEntry]s, [KtTryExpression]s, [KtWhenExpression]s, [KtIfExpression]s and other [KtElement]s.
   * @see org.jetbrains.kotlin.idea.codeInsight.KotlinExpressionTypeProvider.shouldShowType for all covered [KtElement]s
   * @param informationHint resolves and render's the Type of the evaluated expression
   * @param expressionAt provides all [KtExpression]s where a TypeHint should appear
   * @param errorHint if the Type can't be detected
   * @sample [org.jetbrains.kotlin.idea.codeInsight.KotlinExpressionTypeProvider]
   */
  fun IdeMetaPlugin.addExpressionTypeProviderForKotlin(
    informationHint: BindingContext.(expression: KtExpression) -> String,
    expressionAt: (elementAt: PsiElement) -> List<KtExpression>,
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
    expressionAt: (elementAt: PsiElement) -> List<A>,
    errorHint: String = "No expression Found",
    hasAdvancedInformation: Boolean = false,
    advancedInformation: (expression: A) -> String = Noop.string1()
  ): ExtensionPhase =
    extensionProvider(
      LanguageExpressionTypes.INSTANCE,
      object : ExpressionTypeProvider<A>() {
        override fun getInformationHint(element: A): String = informationHint(element)
        override fun hasAdvancedInformation(): Boolean = hasAdvancedInformation
        override fun getExpressionsAt(elementAt: PsiElement): MutableList<A> = expressionAt(elementAt).toMutableList()
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
   * The following example provides Hints for [ClassDescriptor] from [org.jetbrains.kotlin.idea.parameterInfo.KotlinClassTypeArgumentInfoHandler]:
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import org.jetbrains.kotlin.descriptors.ClassDescriptor
   * import org.jetbrains.kotlin.idea.references.resolveMainReferenceToDescriptors
   * import org.jetbrains.kotlin.psi.KtTypeArgumentList
   * import org.jetbrains.kotlin.psi.KtUserType
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * //sampleStart
   * val IdeMetaPlugin.parameterHints: Plugin
   *  get() =  "Hints for ClassDescriptor" {
   *   meta(
   *    addParameterInfoHandlerForKotlin(
   *     fetchTypeParameters = { descriptor: ClassDescriptor -> descriptor.typeConstructor.parameters },
   *     findParameterOwners = { argumentList: KtTypeArgumentList ->
   *      argumentList.parent?.safeAs<KtUserType>()?.referenceExpression?.resolveMainReferenceToDescriptors()?.mapNotNull { it.safeAs<ClassDescriptor>() }
   *     },
   *     argumentListAllowedParentClasses = setOf(KtUserType::class.java)
   *    )
   *   )
   *  }
   * //sampleEnd
   * ```
   * @see addParameterInfoHandlerForKotlin
   *
   */
  fun <Type : DeclarationDescriptor, A> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    fetchTypeParameters: (descriptor: Type) -> List<TypeParameterDescriptor>,
    findParameterOwners: (argumentList: KtTypeArgumentList) -> Collection<Type>?,
    argumentListAllowedParentClasses: Set<Class<A>>
  ): ExtensionPhase =
    addParameterInfoHandler(
      object : KotlinTypeArgumentInfoHandlerBase<Type>() {
        override fun fetchTypeParameters(descriptor: Type): List<TypeParameterDescriptor> =
          fetchTypeParameters(descriptor)

        override fun findParameterOwners(argumentList: KtTypeArgumentList): Collection<Type>? =
          findParameterOwners(argumentList)

        override fun getArgumentListAllowedParentClasses(): MutableSet<Class<A>> =
          argumentListAllowedParentClasses.toMutableSet()
      }
    )

  /**
   * registers a [KotlinParameterInfoWithCallHandlerBase]
   * This is used for [FunctionDescriptor]'s, the `Owner` is [ArgumentList] and the `ActualType` is [Argument]. Naturally, `Type` is a [FunctionDescriptor].
   * The following example is for [KtLambdaArgument]s from [org.jetbrains.kotlin.idea.parameterInfo.KotlinLambdaParameterInfoHandler]:
   * ```kotlin:ank:playground
   * import arrow.meta.Plugin
   * import arrow.meta.ide.IdeMetaPlugin
   * import arrow.meta.invoke
   * import org.jetbrains.kotlin.lexer.KtTokens
   * import org.jetbrains.kotlin.psi.KtCallElement
   * import org.jetbrains.kotlin.psi.KtLambdaArgument
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * //sampleStart
   * val IdeMetaPlugin.parameterHints: Plugin
   *  get() =  "Hints for FunctionDescriptor" {
   *   meta(
   *    addParameterInfoHandlerForKotlin(
   *     argument = KtLambdaArgument::class,
   *     argumentList = KtLambdaArgument::class,
   *     actualParameters = { arrayOf(it) },
   *     actualParametersRBraceType = KtTokens.RBRACE,
   *     argumentListAllowedParentClasses = setOf(KtLambdaArgument::class.java),
   *     parameterIndex = { _, argumentList ->
   *      argumentList.parent?.safeAs<KtCallElement>()?.valueArguments?.size?.dec() ?: 0
   *     }
   *    )
   *   )
   *  }
   * //sampleEnd
   * ```
   * @see addParameterInfoHandlerForKotlin
   * @see KotlinParameterInfoWithCallHandlerBase and its SubType.
   * @param parameterIndex default count's each occurrence of a [KtTokens.COMMA]
   */
  @Suppress
  fun <ArgumentList : KtElement, Argument : KtElement, A> IdeMetaPlugin.addParameterInfoHandlerForKotlin(
    argumentList: KClass<ArgumentList>,
    argument: KClass<Argument>,
    actualParameters: (o: ArgumentList) -> Array<Argument>,
    actualParametersRBraceType: IElementType,
    argumentListAllowedParentClasses: Set<Class<A>>,
    parameterIndex: (ctx: UpdateParameterInfoContext, argumentList: ArgumentList) -> Int =
      { ctx, list -> list.allChildren.takeWhile { it.startOffset < ctx.offset }.count { it.node.elementType == KtTokens.COMMA } }
  ): ExtensionPhase =
    addParameterInfoHandler(
      object : KotlinParameterInfoWithCallHandlerBase<ArgumentList, Argument>(argumentList, argument) {
        override fun getParameterIndex(context: UpdateParameterInfoContext, argumentList: ArgumentList): Int =
          parameterIndex(context, argumentList)

        override fun getActualParameters(o: ArgumentList): Array<Argument> =
          actualParameters(o)

        override fun getActualParametersRBraceType(): IElementType =
          actualParametersRBraceType

        override fun getArgumentListAllowedParentClasses(): MutableSet<Class<A>> =
          argumentListAllowedParentClasses.toMutableSet()
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

  /**
   * Used to modify a [DescriptorRenderer] for type Hinting
   */
  fun HintingSyntax.classifierNamePolicy(
    render: (classifier: ClassifierDescriptor, renderer: DescriptorRenderer) -> String =
      { classifier, renderer ->
        if (DescriptorUtils.isAnonymousObject(classifier)) "&lt;anonymous object&gt;" else ClassifierNamePolicy.SHORT.renderClassifier(classifier, renderer)
      }
  ): ClassifierNamePolicy =
    object : ClassifierNamePolicy {
      override fun renderClassifier(classifier: ClassifierDescriptor, renderer: DescriptorRenderer): String = render(classifier, renderer)
    }
}
