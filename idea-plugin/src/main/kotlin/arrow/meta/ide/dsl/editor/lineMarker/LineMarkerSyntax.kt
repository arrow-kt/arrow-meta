package arrow.meta.ide.dsl.editor.lineMarker

import arrow.meta.ide.MetaIde
import arrow.meta.ide.dsl.utils.descriptorRender
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.codeInsight.daemon.MergeableLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import java.awt.event.MouseEvent
import javax.swing.Icon

/**
 * LineMarker's serve as visuals, which appear on specified PsiElements.
 * There are several methods to subscribe LineMarkers, the one [LineMarkerSyntax] provides is derived from Kotlin's
 * [org.jetbrains.kotlin.idea.highlighter.KotlinSuspendCallLineMarkerProvider] and [org.jetbrains.kotlin.idea.highlighter.KotlinRecursiveCallLineMarkerProvider].
 * In general, subscription techniques differ mainly in performance.
 */
interface LineMarkerSyntax {
  // TODO: Add more Techniques such as the one from Elm

  fun MetaIde.registerLineMarker(provider: LineMarkerProvider): ExtensionPhase =
    extensionProvider(LineMarkerProviders.getInstance(), provider)

  fun <A : PsiElement> MetaIde.addLineMarkerProvider(
    transform: (PsiElement) -> A?,
    lineMarkerInfo: (a: A) -> LineMarkerInfo<PsiElement>?,
    slowLineMarker: (a: A) -> LineMarkerInfo<PsiElement>? = Noop.nullable1()
  ): ExtensionPhase =
    registerLineMarker(
      object : LineMarkerProvider {
        override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiElement>? =
          transform(element)?.let(lineMarkerInfo)

        override fun collectSlowLineMarkers(elements: MutableList<PsiElement>, result: MutableCollection<LineMarkerInfo<PsiElement>>) {
          for (element: A in elements.mapNotNull { transform(it) }) {
            ProgressManager.checkCanceled()
            slowLineMarker(element)?.let { result.add(it) }
          }
        }
      }
    )

  /**
   * Due tu performance reason's it is advised that [A] is a leaf element (e.g: Psi(Identifier)) and not composite PsiElements such as [KtClass].
   * The identifying PsiElement of the latter is the class name. The PsiViewer Plugin may help to verify that [A] is a leaf element, by observing the tree structure of the PsiElement.
   * Nonetheless, IntelliJ will automatically send warnings during the `runIde` gradle task, if an implementation doesn't comply with this premise.
   * @see [com.intellij.codeInsight.daemon.LineMarkerProvider] for more information
   * @sample [arrow.meta.ide.plugins.optics.opticsIdePlugin]
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiElement> MetaIde.addLineMarkerProvider(
    icon: Icon,
    transform: (PsiElement) -> A?,
    message: (element: A) -> String = Noop.string1(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    clickAction: AnAction? = null
  ): ExtensionPhase =
    addLineMarkerProvider(
      transform,
      { lineMarkerInfo(icon, it, message as (PsiElement) -> String, placed, navigate, clickAction) }
    )

  @Suppress("UNCHECKED_CAST")
  fun <A : PsiElement, B : PsiElement> MetaIde.addRelatedLineMarkerProvider(
    icon: Icon,
    transform: (PsiElement) -> A?,
    targets: (A) -> List<B>,
    message: DescriptorRenderer.Companion.(A, targets: List<B>) -> String? = Noop.nullable3(),
    cellRenderer: PsiElementListCellRenderer<B> = DefaultPsiElementCellRenderer() as PsiElementListCellRenderer<B>,
    popUpTitle: DescriptorRenderer.Companion.(A, targets: List<B>) -> String? = Noop.string3(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
  ): ExtensionPhase =
    relatedLineMarkerProvider(
      transform,
      { element: A ->
        val list: List<B> = targets(element)
        navigationGutter(icon, element, targets) { element: A ->
          setCellRenderer(cellRenderer)
          setTarget(element)
          popUpTitle(DescriptorRenderer.Companion, element, list)?.let(::setPopupTitle)
          message(DescriptorRenderer.Companion, element, list)?.let(::setTooltipText)
          setAlignment(placed)
          createLineMarkerInfo(element)
        }
      }
    )

  /**
   * Similar to [addLineMarkerProvider], but with mergeable LineMarkers, based on the predicate [mergeWith].
   * @param commonIcon defines the common Icon after the merge
   * @param navigate this function allows you to execute anything based on your use-case: actions, manipulations to PsiElements, opening Files or anything else.
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiElement> MetaIde.addLineMarkerProviderM(
    icon: Icon,
    transform: (PsiElement) -> A?,
    message: (element: A) -> String = Noop.string1(),
    commonIcon: MergeableLineMarkerInfo<PsiElement>.(others: List<MergeableLineMarkerInfo<PsiElement>>) -> Icon = { icon },
    mergeWith: MergeableLineMarkerInfo<PsiElement>.(other: MergeableLineMarkerInfo<*>) -> Boolean = { this.icon == it.icon },
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    clickAction: AnAction? = null
  ): ExtensionPhase =
    addLineMarkerProvider(
      transform,
      { mergeableLineMarkerInfo(icon, it, message as (PsiElement) -> String, commonIcon, mergeWith, placed, navigate, clickAction) }
    )

  /**
   * provides a function [f] from a Leaf PsiElement
   * @receiver is the Leaf PsiElement
   */
  fun <A : PsiElement, L : LineMarkerInfo<PsiElement>> PsiElement.onComposite(composite: Class<A>, f: (A) -> L): L? =
    PsiTreeUtil.getParentOfType(this, composite)?.let(f)

  /**
   * [addLineMarkerProvider] is a convenience extension, which registers the Leaf element of a composite PsiElement [A] e.g.: `KtClass`
   * and circumvents effort's to find the right PsiElement.
   * In addition, plugin developer's can compose sophisticated messages, as the whole scope of [A] can be exploited.
   * ```kotlin:ank:playground
   * import arrow.meta.ide.IdePlugin
   * import arrow.meta.ide.MetaIde
   * import arrow.meta.ide.resources.ArrowIcons
   * import arrow.meta.ide.invoke
   * import com.intellij.psi.PsiElement
   * import org.jetbrains.kotlin.idea.caches.resolve.resolveToDescriptorIfAny
   * import org.jetbrains.kotlin.psi.KtNamedFunction
   * import org.jetbrains.kotlin.renderer.ParameterNameRenderingPolicy
   * import org.jetbrains.kotlin.utils.addToStdlib.safeAs
   *
   * val MetaIde.descriptorLineMarker: IdePlugin
   *   get() = "Render Descriptor Plugin" {
   *     meta(
   *       addLineMarkerProvider(
   *         transform = { e: PsiElement -> e.safeAs() },
   *         composite = KtNamedFunction::class.java,
   *         icon = ArrowIcons.ICON2,
   *         message = { f ->
   *           HTML.withOptions { // check out DescriptorRenderer's companion for more options
   *             unitReturnType = true // renders Unit Type
   *             classifierNamePolicy = classifierNamePolicy() // define your own policies
   *             parameterNameRenderingPolicy = ParameterNameRenderingPolicy.ONLY_NON_SYNTHESIZED
   *           }.let { renderer ->
   *             f.resolveToDescriptorIfAny()?.let(renderer::render) ?: "Unresolved Descriptor"
   *           }
   *         }
   *       )
   *     )
   *   }
   * ```
   * @param composite In Contrast, lineMarkers constructed without this parameter have a clearly constrained message.
   * @param message you may use a [DescriptorRenderer] for rendering descriptors see [descriptorRender]
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiNameIdentifierOwner> MetaIde.addLineMarkerProvider(
    icon: Icon,
    transform: (PsiElement) -> A?,
    composite: Class<A>,
    message: DescriptorRenderer.Companion.(A) -> String = Noop.string2(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    clickAction: AnAction? = null
  ): ExtensionPhase =
    addLineMarkerProvider(
      { transform(it)?.identifyingElement },
      {
        it.onComposite(composite) { psi: A ->
          lineMarkerInfo(icon, it, { message(DescriptorRenderer.Companion, psi) }, placed, navigate, clickAction)
        }
      }
    )

  /**
   * Algebra notes
   * // com.intellij.psi.SmartPointerManager as the receiver #createSmartPsiElementPointer {
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiNameIdentifierOwner, B : PsiElement> MetaIde.addRelatedLineMarkerProvider(
    icon: Icon,
    transform: (PsiElement) -> A?,
    composite: Class<A>,
    targets: (A) -> List<B>,
    message: DescriptorRenderer.Companion.(A, targets: List<B>) -> String? = Noop.nullable3(),
    cellRenderer: PsiElementListCellRenderer<B> = DefaultPsiElementCellRenderer() as PsiElementListCellRenderer<B>,
    popUpTitle: DescriptorRenderer.Companion.(A, targets: List<B>) -> String? = Noop.string3(),
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT
  ): ExtensionPhase =
    relatedLineMarkerProvider(
      { transform(it)?.identifyingElement },
      {
        it.onComposite(composite) { psi: A ->
          val list: List<B> = targets(psi)
          navigationGutter(icon, psi, targets) { element: A ->
            setCellRenderer(cellRenderer)
            setTarget(element)
            popUpTitle(DescriptorRenderer.Companion, element, list)?.let(::setPopupTitle)
            message(DescriptorRenderer.Companion, element, list)?.let(::setTooltipText)
            setAlignment(placed)
            createLineMarkerInfo(it)
          }
        }
      }
    )

  /**
   * Similar to [addLineMarkerProvider], but with mergeable LineMarkers, based on the predicate [mergeWith].
   * @param commonIcon defines the common Icon after the merge
   * @param navigate this function allows you to execute anything based on your use-case: actions, manipulations to PsiElements, opening Files or anything else.
   */
  @Suppress("UNCHECKED_CAST")
  fun <A : PsiNameIdentifierOwner> MetaIde.addLineMarkerProviderM(
    icon: Icon,
    transform: (PsiElement) -> A?,
    composite: Class<A>,
    message: DescriptorRenderer.Companion.(A) -> String = Noop.string2(),
    commonIcon: MergeableLineMarkerInfo<PsiElement>.(others: List<MergeableLineMarkerInfo<PsiElement>>) -> Icon = { icon },
    mergeWith: MergeableLineMarkerInfo<PsiElement>.(other: MergeableLineMarkerInfo<*>) -> Boolean = { this.icon == it.icon },
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.RIGHT,
    clickAction: AnAction? = null
  ): ExtensionPhase =
    addLineMarkerProvider(
      { transform(it)?.identifyingElement },
      {
        it.onComposite(composite) { psi: A ->
          mergeableLineMarkerInfo(icon, it, { message(DescriptorRenderer.Companion, psi) }, commonIcon, mergeWith, placed, navigate, clickAction)
        }
      }
    )

  /**
   * @param clickAction if null this will place a breakpoint on a mouse click otherwise it executes the action
   * @param navigate this function allows you to execute anything based on your use-case: actions, manipulations to PsiElements, opening Files or anything else.
   */
  fun LineMarkerSyntax.lineMarkerInfo(
    icon: Icon,
    element: PsiElement,
    message: (PsiElement) -> String,
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT,
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    clickAction: AnAction? = null
  ): LineMarkerInfo<PsiElement> =
    object : LineMarkerInfo<PsiElement>(element, element.textRange, icon, message, navigate, placed) {
      override fun createGutterRenderer(): GutterIconRenderer =
        object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
          override fun getClickAction(): AnAction? = clickAction
        }
    }

  /**
   * creates a NavigationGutter
   */
  fun <A : PsiElement, R> LineMarkerSyntax.navigationGutter(
    icon: Icon,
    element: A,
    config: NavigationGutterIconBuilder<PsiElement>.(A) -> R
  ): R =
    NavigationGutterIconBuilder.create(icon).config(element)

  fun <A, R> LineMarkerSyntax.navigationGutter(
    icon: Icon,
    element: A,
    targets: (A) -> List<PsiElement>,
    config: NavigationGutterIconBuilder<A>.(A) -> R
  ): R =
    NavigationGutterIconBuilder.create(icon, targets).config(element)

  /**
   * `MergeableLineMarkerInfo` can merge multiple LineMarkerInfo's into one, if [mergeWith] is true.
   * @param commonIcon defines the common Icon after the merge
   * @param navigate this function allows you to execute anything based on your use-case: actions, manipulations to PsiElements, opening Files or anything else.
   */
  fun LineMarkerSyntax.mergeableLineMarkerInfo(
    icon: Icon,
    element: PsiElement,
    message: (PsiElement) -> String,
    commonIcon: MergeableLineMarkerInfo<PsiElement>.(others: List<MergeableLineMarkerInfo<PsiElement>>) -> Icon = { icon },
    mergeWith: MergeableLineMarkerInfo<PsiElement>.(other: MergeableLineMarkerInfo<*>) -> Boolean = { this.icon == it.icon },
    placed: GutterIconRenderer.Alignment = GutterIconRenderer.Alignment.LEFT,
    navigate: (event: MouseEvent, element: PsiElement) -> Unit = Noop.effect2,
    clickAction: AnAction? = null
  ): MergeableLineMarkerInfo<PsiElement> =
    object : MergeableLineMarkerInfo<PsiElement>(element, element.textRange, icon, message, navigate, placed) {
      override fun canMergeWith(info: MergeableLineMarkerInfo<*>): Boolean =
        mergeWith(info)

      override fun getCommonIcon(infos: MutableList<MergeableLineMarkerInfo<PsiElement>>): Icon =
        commonIcon(infos.toList())

      override fun createGutterRenderer(): GutterIconRenderer =
        object : LineMarkerInfo.LineMarkerGutterIconRenderer<PsiElement>(this) {
          override fun getClickAction(): AnAction? = clickAction
        }
    }

  /**
   * registers a RelatedItemLineMarkerProvider
   */
  fun <A : PsiElement> MetaIde.relatedLineMarkerProvider(
    transform: (PsiElement) -> A?,
    lineMarkerInfo: (a: A) -> RelatedItemLineMarkerInfo<PsiElement>?
  ): ExtensionPhase =
    registerLineMarker(
      object : RelatedItemLineMarkerProvider() {
        override fun getLineMarkerInfo(element: PsiElement): RelatedItemLineMarkerInfo<PsiElement>? =
          transform(element)?.let(lineMarkerInfo)
      }
    )
}
