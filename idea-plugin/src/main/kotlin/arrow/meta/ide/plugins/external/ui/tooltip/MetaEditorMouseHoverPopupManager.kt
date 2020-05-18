// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
@file:Suppress("UnstableApiUsage")

package arrow.meta.ide.plugins.external.ui.tooltip

import arrow.meta.ide.plugins.external.ui.tooltip.util.isArrowMetaTooltip
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.tooltips.TooltipActionProvider
import com.intellij.codeInsight.documentation.DocumentationComponent
import com.intellij.codeInsight.documentation.DocumentationManager
import com.intellij.codeInsight.hint.HintManagerImpl.ActionToIgnore
import com.intellij.codeInsight.hint.LineTooltipRenderer
import com.intellij.codeInsight.hint.TooltipGroup
import com.intellij.codeInsight.lookup.LookupManager
import com.intellij.ide.IdeEventQueue
import com.intellij.ide.actions.BaseNavigateToSourceAction
import com.intellij.injected.editor.DocumentWindow
import com.intellij.injected.editor.EditorWindow
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.application.ModalityStateListener
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.impl.LaterInvocator
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.VisualPosition
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseListener
import com.intellij.openapi.editor.event.EditorMouseMotionListener
import com.intellij.openapi.editor.event.VisibleAreaEvent
import com.intellij.openapi.editor.event.VisibleAreaListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.EditorMarkupModel
import com.intellij.openapi.editor.ex.EditorSettingsExternalizable
import com.intellij.openapi.editor.ex.TooltipAction
import com.intellij.openapi.editor.ex.util.EditorUtil
import com.intellij.openapi.editor.impl.EditorMouseHoverPopupControl
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.util.ProgressIndicatorBase
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.openapi.ui.popup.util.PopupUtil
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.registry.Registry
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowId
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiPlainText
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil
import com.intellij.reference.SoftReference
import com.intellij.ui.HintHint
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.LightweightHint
import com.intellij.ui.MouseMovementTracker
import com.intellij.ui.SideBorder
import com.intellij.ui.WidthBasedLayout
import com.intellij.ui.popup.AbstractPopup
import com.intellij.ui.popup.PopupFactoryImpl
import com.intellij.util.Alarm
import com.intellij.util.concurrency.AppExecutorUtil
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import org.jetbrains.concurrency.CancellablePromise
import java.awt.AWTEvent
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.KeyboardFocusManager
import java.awt.LayoutManager
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseEvent
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.MenuSelectionManager

@Service
class MetaEditorMouseHoverPopupManager : Disposable {
  private val myAlarm: Alarm
  private val myMouseMovementTracker = MouseMovementTracker()
  private var myKeepPopupOnMouseMove = false
  private var myCurrentEditor: WeakReference<Editor>? = null
  private var myPopupReference: WeakReference<AbstractPopup>? = null
  private var myContext: Context? = null
  private var myCurrentProgress: ProgressIndicator? = null
  private var myPreparationTask: CancellablePromise<Context?>? = null
  private var mySkipNextMovement = false
  override fun dispose() {}
  private fun handleMouseMoved(e: EditorMouseEvent) {
    val startTimestamp = System.currentTimeMillis()
    cancelCurrentProcessing()
    if (ignoreEvent(e)) return
    val editor = e.editor
    if (isPopupDisabled(editor)) {
      closeHint()
      return
    }
    val targetOffset = getTargetOffset(e)
    if (targetOffset < 0) {
      closeHint()
      return
    }
    myPreparationTask = ReadAction.nonBlocking<Context?> { createContext(editor, targetOffset, startTimestamp) }
      .coalesceBy(this)
      .withDocumentsCommitted(requireNotNull(editor.project))
      .expireWhen { editor.isDisposed }
      .finishOnUiThread(ModalityState.any()) { context: Context? ->
        myPreparationTask = null
        if (context == null || !editor.contentComponent.isShowing) {
          closeHint()
          return@finishOnUiThread
        }
        val relation = if (isHintShown()) context.compareTo(myContext) else Context.Relation.DIFFERENT
        if (relation == Context.Relation.SAME) {
          return@finishOnUiThread
        } else if (relation == Context.Relation.DIFFERENT) {
          closeHint()
        }
        scheduleProcessing(editor, context, relation == Context.Relation.SIMILAR, false, false)
      }
      .submit(AppExecutorUtil.getAppExecutorService())
  }

  private fun cancelCurrentProcessing() {
    if (myPreparationTask != null) {
      myPreparationTask!!.cancel()
      myPreparationTask = null
    }
    myAlarm.cancelAllRequests()
    if (myCurrentProgress != null) {
      myCurrentProgress!!.cancel()
      myCurrentProgress = null
    }
  }

  private fun skipNextMovement() {
    mySkipNextMovement = true
  }

  private fun scheduleProcessing(editor: Editor,
                                 context: Context,
                                 updateExistingPopup: Boolean,
                                 forceShowing: Boolean,
                                 requestFocus: Boolean) {
    val progress = ProgressIndicatorBase()
    myCurrentProgress = progress
    myAlarm.addRequest({
      ProgressManager.getInstance().executeProcessUnderProgress({
        val info = context.calcInfo(editor)
        ApplicationManager.getApplication().invokeLater {
          if (progress !== myCurrentProgress) {
            return@invokeLater
          }
          myCurrentProgress = null
          if (info == null ||
            !editor.contentComponent.isShowing ||
            !forceShowing && isPopupDisabled(editor)) {
            return@invokeLater
          }
          val popupBridge = PopupBridge()
          val component = info.createComponent(editor, popupBridge, requestFocus)
          if (component == null) {
            closeHint()
          } else {
            if (updateExistingPopup && isHintShown()) {
              updateHint(component, popupBridge)
            } else {
              val hint = createHint(component, popupBridge, requestFocus)
              showHintInEditor(hint, editor, context)
              myPopupReference = WeakReference(hint)
              myCurrentEditor = WeakReference(editor)
            }
            myContext = context
          }
        }
      }, progress)
    }, context.showingDelay)
  }

  private fun onActivity() {
    if (!Registry.`is`("editor.new.mouse.hover.popups")) return
    cancelCurrentProcessing()
  }

  private fun ignoreEvent(e: EditorMouseEvent): Boolean {
    if (mySkipNextMovement) {
      mySkipNextMovement = false
      return true
    }
    val currentHintBounds = getCurrentHintBounds(e.editor)
    return myMouseMovementTracker.isMovingTowards(e.mouseEvent, currentHintBounds) ||
      currentHintBounds != null && myKeepPopupOnMouseMove
  }

  private fun getCurrentHintBounds(editor: Editor): Rectangle? {
    val popup = getCurrentHint() ?: return null
    val size = popup.size ?: return null
    val result = Rectangle(popup.locationOnScreen, size)
    val borderTolerance = editor.lineHeight / 3
    result.grow(borderTolerance, borderTolerance)
    return result
  }

  private fun showHintInEditor(hint: AbstractPopup, editor: Editor, context: Context) {
    closeHint()
    myMouseMovementTracker.reset()
    myKeepPopupOnMouseMove = false
    editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POSITION, context.getPopupPosition(editor))
    try {
      hint.showInBestPositionFor(editor)
    } finally {
      editor.putUserData(PopupFactoryImpl.ANCHOR_POPUP_POSITION, null)
    }
    val window = hint.popupWindow
    if (window != null) {
      window.focusableWindowState = true
      IdeEventQueue.getInstance().addDispatcher(IdeEventQueue.EventDispatcher { e: AWTEvent ->
        if (e.id == MouseEvent.MOUSE_PRESSED && e.source === window) {
          myKeepPopupOnMouseMove = true
        }
        false
      }, hint)
    }
  }

  private fun updateHint(component: JComponent, popupBridge: PopupBridge) {
    val popup = getCurrentHint()
    if (popup != null) {
      val wrapper = popup.component as WrapperPanel
      wrapper.setContent(component)
      validatePopupSize(popup)
      popupBridge.setPopup(popup)
    }
  }

  private fun cancelProcessingAndCloseHint() {
    cancelCurrentProcessing()
    closeHint()
  }

  private fun closeHint() {
    val hint = getCurrentHint()
    hint?.cancel()
    myPopupReference = null
    myCurrentEditor = null
    myContext = null
  }

  private fun isHintShown(): Boolean {
    return getCurrentHint() != null
  }

  private fun getCurrentHint(): AbstractPopup? {
    if (myPopupReference == null) return null
    val hint = myPopupReference!!.get()
    if (hint == null || !hint.isVisible) {
      hint?.cancel()
      myPopupReference = null
      myCurrentEditor = null
      myContext = null
      return null
    }
    return hint
  }

  fun showInfoTooltip(editor: Editor,
                      info: HighlightInfo,
                      offset: Int,
                      requestFocus: Boolean,
                      showImmediately: Boolean) {
    if (editor.project == null) return
    cancelProcessingAndCloseHint()
    val context: Context = object : Context(System.currentTimeMillis(), offset, info, null) {
      override val showingDelay: Long
        get() = if (showImmediately) 0 else super.showingDelay
    }
    scheduleProcessing(editor, context, false, true, requestFocus)
  }

  private open class Context(private val startTimestamp: Long, private val targetOffset: Int, highlightInfo: HighlightInfo?, elementForQuickDoc: PsiElement?) {
    private val highlightInfo: WeakReference<HighlightInfo>?
    private val elementForQuickDoc: WeakReference<PsiElement>?
    private fun getElementForQuickDoc(): PsiElement? {
      return SoftReference.dereference(elementForQuickDoc)
    }

    private fun getHighlightInfo(): HighlightInfo? {
      return SoftReference.dereference(highlightInfo)
    }

    internal fun compareTo(other: Context?): Relation {
      if (other == null) return Relation.DIFFERENT
      val highlightInfo = getHighlightInfo()
      if (highlightInfo != other.getHighlightInfo()) return Relation.DIFFERENT
      return if (getElementForQuickDoc() == other.getElementForQuickDoc()) Relation.SAME else if (highlightInfo == null) Relation.DIFFERENT else Relation.SIMILAR
    }

    open val showingDelay: Long
      get() = Math.max(0, EditorSettingsExternalizable.getInstance().tooltipsDelay - (System.currentTimeMillis() - startTimestamp))

    fun getPopupPosition(editor: Editor): VisualPosition {
      val highlightInfo = getHighlightInfo()
      return if (highlightInfo == null) {
        var offset = targetOffset
        val elementForQuickDoc = getElementForQuickDoc()
        if (elementForQuickDoc != null && elementForQuickDoc.isValid) {
          offset = getElementStartHostOffset(elementForQuickDoc)
        }
        editor.offsetToVisualPosition(offset)
      } else {
        val targetPosition = editor.offsetToVisualPosition(targetOffset)
        val endPosition = editor.offsetToVisualPosition(highlightInfo.getEndOffset())
        if (endPosition.line <= targetPosition.line) return targetPosition
        val targetPoint = editor.visualPositionToXY(targetPosition)
        val endPoint = editor.visualPositionToXY(endPosition)
        val resultPoint = Point(targetPoint.x, if (endPoint.x > targetPoint.x) endPoint.y else editor.visualLineToY(endPosition.line - 1))
        editor.xyToVisualPosition(resultPoint)
      }
    }

    fun calcInfo(editor: Editor): Info? {
      val info = getHighlightInfo()
      var infoToUse: HighlightInfo? = null
      var tooltipAction: TooltipAction? = null
      if (info != null && info.description != null && info.toolTip != null) {
        infoToUse = info
        try {
          tooltipAction = ReadAction.nonBlocking<TooltipAction?> { TooltipActionProvider.calcTooltipAction(info, editor) }.executeSynchronously()
        } catch (ignored: IndexNotReadyException) {
        } catch (e: ProcessCanceledException) {
          throw e
        } catch (e: Exception) {
          LOG.warn(e)
        }
      }
      var quickDocMessage: String? = null
      var targetElement: PsiElement? = null
      val element = getElementForQuickDoc()
      if (element != null) {
        try {
          val project: Project = requireNotNull(editor.project)
          val documentationManager = ReadAction.compute<DocumentationManager?, RuntimeException> { if (project.isDisposed) null else DocumentationManager.getInstance(project) }
          if (documentationManager != null) {
            targetElement = ReadAction.nonBlocking<PsiElement?> {
              if (element.isValid) {
                val containingFile = element.containingFile
                val injectedEditor = InjectedLanguageUtil.getInjectedEditorForInjectedFile(editor, null, containingFile)
                val offset = if (injectedEditor is EditorWindow) injectedEditor.document.hostToInjected(targetOffset) else targetOffset
                return@nonBlocking documentationManager.findTargetElement(injectedEditor, offset, containingFile, element)
              }
              null
            }.executeSynchronously()
            if (targetElement != null) {
              quickDocMessage = documentationManager.generateDocumentation(targetElement, element, true)
            }
          }
        } catch (ignored: IndexNotReadyException) {
        } catch (ignored: ProcessCanceledException) {
        } catch (e: Exception) {
          LOG.warn(e)
        }
      }
      return if (infoToUse == null && quickDocMessage == null) null else Info(infoToUse, tooltipAction, quickDocMessage, targetElement)
    }

    internal enum class Relation {
      SAME,  // no need to update popup
      SIMILAR,  // popup needs to be updated
      DIFFERENT // popup needs to be closed, and new one shown
    }

    companion object {
      private fun getElementStartHostOffset(element: PsiElement): Int {
        val offset = element.textRange.startOffset
        val project = element.project
        val containingFile = element.containingFile
        if (containingFile != null && InjectedLanguageManager.getInstance(project).isInjectedFragment(containingFile)) {
          val document = PsiDocumentManager.getInstance(project).getDocument(containingFile)
          if (document is DocumentWindow) {
            return document.injectedToHost(offset)
          }
        }
        return offset
      }
    }

    init {
      this.highlightInfo = highlightInfo?.let { WeakReference(it) }
      this.elementForQuickDoc = elementForQuickDoc?.let { WeakReference(it) }
    }
  }

  private class Info(highlightInfo: HighlightInfo?, tooltipAction: TooltipAction?, quickDocMessage: String?, quickDocElement: PsiElement?) {
    private val highlightInfo: HighlightInfo?
    private val tooltipAction: TooltipAction?
    private val quickDocMessage: String?
    private val quickDocElement: WeakReference<PsiElement?>
    fun createComponent(editor: Editor, popupBridge: PopupBridge, requestFocus: Boolean): JComponent? {
      val quickDocShownInPopup = quickDocMessage != null &&
        ToolWindowManager.getInstance(requireNotNull(editor.project))
          .getToolWindow(ToolWindowId.DOCUMENTATION) == null
      val c1 = createHighlightInfoComponent(editor, !quickDocShownInPopup, popupBridge, requestFocus)
      val c2 = createQuickDocComponent(editor, c1 != null, popupBridge)
      assert(quickDocShownInPopup == (c2 != null))
      if (c1 == null && c2 == null) return null
      val p = JPanel(CombinedPopupLayout(c1, c2))
      p.border = null
      if (c1 != null) p.add(c1)
      if (c2 != null) p.add(c2)
      return p
    }

    private fun createHighlightInfoComponent(editor: Editor,
                                             highlightActions: Boolean,
                                             popupBridge: PopupBridge,
                                             requestFocus: Boolean): JComponent? {
      if (highlightInfo == null) return null
      val provider = (editor.markupModel as EditorMarkupModel).errorStripTooltipRendererProvider
      val tooltipRenderer = provider.calcTooltipRenderer(Objects.requireNonNull(highlightInfo.toolTip)!!, tooltipAction, -1) as? LineTooltipRenderer
        ?: return null
      val metaTooltipRenderer = MetaTooltipRenderer(highlightInfo.description, arrayOf(), tooltipAction)
      val renderer: LineTooltipRenderer
      renderer = if (highlightInfo.description.isArrowMetaTooltip()) {
        metaTooltipRenderer
      } else {
        tooltipRenderer
      }
      return createHighlightInfoComponent(editor, renderer, highlightActions, popupBridge, tooltipAction, requestFocus)
    }

    private fun createQuickDocComponent(editor: Editor,
                                        deEmphasize: Boolean,
                                        popupBridge: PopupBridge): DocumentationComponent? {
      if (quickDocMessage == null) return null
      val element = quickDocElement.get()
      val project: Project = requireNotNull(editor.project)
      val documentationManager = DocumentationManager.getInstance(project)
      val toolWindow: ToolWindow? = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.DOCUMENTATION)
      if (toolWindow != null) {
        if (element != null) {
          documentationManager.showJavaDocInfo(editor, element, extractOriginalElement(element)!!, null, quickDocMessage, true, false)
          documentationManager.setAllowContentUpdateFromContext(false)
        }
        return null
      }
      class MyDocComponent : DocumentationComponent(documentationManager, false) {
        override fun showHint() {
          val popup: AbstractPopup? = popupBridge.getPopup()
          popup?.let { validatePopupSize(it) }
        }

        init {
          if (deEmphasize) {
            background = UIUtil.getToolTipActionBackground()
          }
        }
      }

      val component: DocumentationComponent = MyDocComponent()
      if (deEmphasize) {
        component.border = IdeBorderFactory.createBorder(UIUtil.getTooltipSeparatorColor(), SideBorder.TOP)
      }
      component.setData(element, quickDocMessage, null, null, null)
      component.setToolwindowCallback {
        val docElement = component.element
        if (docElement != null) {
          documentationManager.createToolWindow(docElement, extractOriginalElement(docElement))
          val createdToolWindow: ToolWindow? = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.DOCUMENTATION)
          if (createdToolWindow != null) {
            createdToolWindow.isAutoHide = false
          }
        }
        val popup: AbstractPopup? = popupBridge.getPopup()
        popup?.cancel()
      }
      popupBridge.performWhenAvailable(Consumer { hint: AbstractPopup? -> component.hint = hint })
      EditorUtil.disposeWithEditor(editor, component)
      return component
    }

    companion object {
      private fun createHighlightInfoComponent(editor: Editor,
                                               renderer: LineTooltipRenderer,
                                               highlightActions: Boolean,
                                               popupBridge: PopupBridge,
                                               tooltipAction: TooltipAction?,
                                               requestFocus: Boolean): JComponent? {
        val wrapperPanelRef = Ref<WrapperPanel>()
        val mockHintRef = Ref<LightweightHint>()
        val hintHint = HintHint().setAwtTooltip(true).setRequestFocus(requestFocus)
        val hint = renderer.createHint(editor, Point(), false, EDITOR_INFO_GROUP, hintHint, true, highlightActions, false) { expand: Boolean ->
          val newRenderer: LineTooltipRenderer = if (renderer.text != null && renderer.text.isArrowMetaTooltip()) {
            MetaTooltipRenderer(renderer.text!!, arrayOf(), tooltipAction)
          } else {
            renderer.createRenderer(renderer.text, if (expand) 1 else 0)
          }
          val newComponent = createHighlightInfoComponent(editor, newRenderer, highlightActions, popupBridge, tooltipAction, requestFocus)
          val popup: AbstractPopup? = popupBridge.getPopup()
          val wrapper = wrapperPanelRef.get()
          if (newComponent != null && popup != null && wrapper != null) {
            val mockHint = mockHintRef.get()
            mockHint?.let { closeHintIgnoreBinding(it) }
            wrapper.setContent(newComponent)
            validatePopupSize(popup)
          }
        }
          ?: return null
        mockHintRef.set(hint)
        bindHintHiding(hint, popupBridge)
        val component = hint.component
        LOG.assertTrue(component is WidthBasedLayout, "Unexpected type of tooltip component: " + component.javaClass)
        val wrapper = WrapperPanel(component)
        wrapperPanelRef.set(wrapper)
        // emulating LightweightHint+IdeTooltipManager+BalloonImpl - they use the same background
        wrapper.background = hintHint.textBackground
        wrapper.isOpaque = true
        return wrapper
      }

      private fun bindHintHiding(hint: LightweightHint, popupBridge: PopupBridge) {
        val inProcess = AtomicBoolean()
        hint.addHintListener { e: EventObject? ->
          if (hint.getUserData(DISABLE_BINDING) == null && inProcess.compareAndSet(false, true)) {
            try {
              val popup: AbstractPopup? = popupBridge.getPopup()
              popup?.cancel()
            } finally {
              inProcess.set(false)
            }
          }
        }
        popupBridge.performOnCancel(Runnable {
          if (hint.getUserData(DISABLE_BINDING) == null && inProcess.compareAndSet(false, true)) {
            try {
              hint.hide()
            } finally {
              inProcess.set(false)
            }
          }
        })
      }

      private fun closeHintIgnoreBinding(hint: LightweightHint) {
        hint.putUserData(DISABLE_BINDING, java.lang.Boolean.TRUE)
        hint.hide()
      }
    }

    init {
      assert(highlightInfo != null || quickDocMessage != null)
      this.highlightInfo = highlightInfo
      this.tooltipAction = tooltipAction
      this.quickDocMessage = quickDocMessage
      this.quickDocElement = WeakReference(quickDocElement)
    }
  }

  fun getDocumentationComponent(): DocumentationComponent? {
    val hint = getCurrentHint()
    return if (hint == null) null else UIUtil.findComponentOfType(hint.component, DocumentationComponent::class.java)
  }

  private class PopupBridge {
    private var popup: AbstractPopup? = null
    private var consumers: MutableList<Consumer<AbstractPopup>>? = ArrayList()

    fun setPopup(popup: AbstractPopup) {
      assert(this.popup == null)
      this.popup = popup
      consumers?.forEach(Consumer { c: Consumer<AbstractPopup> -> c.accept(popup) })
      consumers = null
    }

    fun getPopup(): AbstractPopup? = popup

    fun performWhenAvailable(consumer: Consumer<AbstractPopup>) {
      if (popup == null) {
        consumers!!.add(consumer)
      } else {
        consumer.accept(popup!!)
      }
    }

    fun performOnCancel(runnable: Runnable) {
      performWhenAvailable(Consumer { popup: AbstractPopup ->
        popup.addListener(object : JBPopupListener {
          override fun onClosed(event: LightweightWindowEvent) {
            runnable.run()
          }
        })
      })
    }
  }

  private class WrapperPanel(content: JComponent) : JPanel(BorderLayout()), WidthBasedLayout {

    init {
      border = null
      setContent(content)
    }

    fun setContent(content: JComponent) {
      removeAll()
      add(content, BorderLayout.CENTER)
    }

    private fun getComponent(): JComponent {
      return getComponent(0) as JComponent
    }

    override fun getPreferredWidth(): Int {
      return WidthBasedLayout.getPreferredWidth(getComponent())
    }

    override fun getPreferredHeight(width: Int): Int {
      return WidthBasedLayout.getPreferredHeight(getComponent(), width)
    }
  }

  private class CombinedPopupLayout(private val highlightInfoComponent: JComponent?, private val quickDocComponent: DocumentationComponent?) : LayoutManager {
    override fun addLayoutComponent(name: String, comp: Component) {}
    override fun removeLayoutComponent(comp: Component) {}
    override fun preferredLayoutSize(parent: Container): Dimension {
      val w1 = WidthBasedLayout.getPreferredWidth(highlightInfoComponent)
      val w2 = WidthBasedLayout.getPreferredWidth(quickDocComponent)
      val preferredWidth = Math.min(JBUI.scale(MAX_POPUP_WIDTH), Math.max(w1, w2))
      val h1 = WidthBasedLayout.getPreferredHeight(highlightInfoComponent, preferredWidth)
      val h2 = WidthBasedLayout.getPreferredHeight(quickDocComponent, preferredWidth)
      return Dimension(preferredWidth, h1 + h2)
    }

    override fun minimumLayoutSize(parent: Container): Dimension {
      val d1 = if (highlightInfoComponent == null) Dimension() else highlightInfoComponent.minimumSize
      val d2 = if (quickDocComponent == null) Dimension() else quickDocComponent.minimumSize
      return Dimension(Math.max(d1.width, d2.width), d1.height + d2.height)
    }

    override fun layoutContainer(parent: Container) {
      val width = parent.width
      val height = parent.height
      if (highlightInfoComponent == null) {
        quickDocComponent?.setBounds(0, 0, width, height)
      } else if (quickDocComponent == null) {
        highlightInfoComponent.setBounds(0, 0, width, height)
      } else {
        val h1 = Math.min(height, highlightInfoComponent.preferredSize.height)
        highlightInfoComponent.setBounds(0, 0, width, h1)
        quickDocComponent.setBounds(0, h1, width, height - h1)
      }
    }

  }

  class EditorMouseMotionEventListener : EditorMouseMotionListener {
    override fun mouseMoved(e: EditorMouseEvent) {
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return
      }
      instance.handleMouseMoved(e)
    }
  }

  class EditorMouseEventListener : EditorMouseListener {
    override fun mouseEntered(event: EditorMouseEvent) {
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return
      }
      // we receive MOUSE_MOVED event after MOUSE_ENTERED even if mouse wasn't physically moved,
// e.g. if a popup overlapping editor has been closed
      instance.skipNextMovement()
    }

    override fun mouseExited(event: EditorMouseEvent) {
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return
      }
      instance.cancelCurrentProcessing()
    }
  }

  private class MyActionListener : AnActionListener {
    override fun beforeActionPerformed(action: AnAction, dataContext: DataContext, event: AnActionEvent) {
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return
      }
      if (action is ActionToIgnore ||
        action is ActionGroup ||
        action is BaseNavigateToSourceAction) return
      instance.cancelProcessingAndCloseHint()
    }

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) {
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return
      }
      instance.cancelProcessingAndCloseHint()
    }
  }

  companion object {
    private val LOG = Logger.getInstance(MetaEditorMouseHoverPopupManager::class.java)
    private val DISABLE_BINDING = Key.create<Boolean>("EditorMouseHoverPopupManager.disable.binding")
    private val EDITOR_INFO_GROUP = TooltipGroup("EDITOR_INFO_GROUP", 0)
    private const val MAX_POPUP_WIDTH = 650
    private fun isPopupDisabled(editor: Editor): Boolean {
      return isAnotherAppInFocus ||
        EditorMouseHoverPopupControl.arePopupsDisabled(editor) || LookupManager.getActiveLookup(editor) != null ||
        isAnotherPopupFocused ||
        isContextMenuShown
    }

    private val isAnotherAppInFocus: Boolean
      private get() = KeyboardFocusManager.getCurrentKeyboardFocusManager().focusedWindow == null

    // e.g. if documentation popup (opened via keyboard shortcut) is already shown
    private val isAnotherPopupFocused: Boolean
      private get() {
        val popup = PopupUtil.getPopupContainerFor(KeyboardFocusManager.getCurrentKeyboardFocusManager().focusOwner)
        return popup != null && !popup.isDisposed
      }

    private val isContextMenuShown: Boolean
      private get() = MenuSelectionManager.defaultManager().selectedPath.size > 0

    private fun createHint(component: JComponent, popupBridge: PopupBridge, requestFocus: Boolean): AbstractPopup {
      val wrapper = WrapperPanel(component)
      val popup = JBPopupFactory.getInstance()
        .createComponentPopupBuilder(wrapper, component)
        .setResizable(true)
        .setFocusable(requestFocus)
        .setRequestFocus(requestFocus)
        .createPopup() as AbstractPopup
      popupBridge.setPopup(popup)
      return popup
    }

    private fun validatePopupSize(popup: AbstractPopup) {
      val component = popup.component
      if (component != null) popup.size = component.preferredSize
    }

    private fun getTargetOffset(event: EditorMouseEvent): Int {
      val editor = event.editor
      val point = event.mouseEvent.point
      if (editor is EditorEx && editor.getProject() != null && event.area === EditorMouseEventArea.EDITING_AREA && event.mouseEvent.modifiers == 0 &&
        EditorUtil.isPointOverText(editor, point) && editor.foldingModel.getFoldingPlaceholderAt(point) == null) {
        val logicalPosition = editor.xyToLogicalPosition(point)
        return editor.logicalPositionToOffset(logicalPosition)
      }
      return -1
    }

    private fun createContext(editor: Editor, offset: Int, startTimestamp: Long): Context? {
      val project: Project = requireNotNull(editor.project)
      var info: HighlightInfo? = null
      if (!Registry.`is`("ide.disable.editor.tooltips")) {
        val daemonCodeAnalyzer = DaemonCodeAnalyzer.getInstance(project) as DaemonCodeAnalyzerImpl
        val highestPriorityOnly = !Registry.`is`("ide.tooltip.showAllSeverities")
        info = daemonCodeAnalyzer
          .findHighlightsByOffset(editor.document, offset, false, highestPriorityOnly, HighlightSeverity.INFORMATION)
      }
      var elementForQuickDoc: PsiElement? = null
      if (EditorSettingsExternalizable.getInstance().isShowQuickDocOnMouseOverElement) {
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        if (psiFile != null) {
          elementForQuickDoc = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, offset)
          if (elementForQuickDoc == null) elementForQuickDoc = psiFile.findElementAt(offset)
          if (elementForQuickDoc is PsiWhiteSpace || elementForQuickDoc is PsiPlainText) {
            elementForQuickDoc = null
          }
        }
      }
      return if (info == null && elementForQuickDoc == null) null else Context(startTimestamp, offset, info, elementForQuickDoc)
    }

    private fun extractOriginalElement(element: PsiElement?): PsiElement? {
      if (element == null) {
        return null
      }
      val originalElementPointer = element.getUserData(DocumentationManager.ORIGINAL_ELEMENT_KEY)
      return originalElementPointer?.element
    }

    val instance: MetaEditorMouseHoverPopupManager
      get() = ServiceManager.getService(MetaEditorMouseHoverPopupManager::class.java)
  }

  init {
    myAlarm = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    val multicaster = EditorFactory.getInstance().eventMulticaster
    multicaster.addCaretListener(object : CaretListener {
      override fun caretPositionChanged(event: CaretEvent) {
        if (!Registry.`is`("editor.new.mouse.hover.popups")) return
        val editor = event.editor
        if (editor === SoftReference.dereference(myCurrentEditor)) {
          DocumentationManager.getInstance(requireNotNull(editor.project)).setAllowContentUpdateFromContext(true)
        }
      }
    }, this)
    multicaster.addVisibleAreaListener(VisibleAreaListener { e: VisibleAreaEvent ->
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return@VisibleAreaListener
      }
      val oldRectangle = e.oldRectangle
      if (e.editor === SoftReference.dereference(myCurrentEditor) && oldRectangle != null && oldRectangle.location != e.newRectangle.location) {
        cancelProcessingAndCloseHint()
      }
    }, this)
    EditorMouseHoverPopupControl.getInstance().addListener {
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return@addListener
      }
      val editor = SoftReference.dereference(myCurrentEditor)
      if (editor != null && EditorMouseHoverPopupControl.arePopupsDisabled(editor)) {
        closeHint()
      }
    }
    LaterInvocator.addModalityStateListener(ModalityStateListener { entering: Boolean ->
      if (!Registry.`is`("editor.new.mouse.hover.popups")) {
        return@ModalityStateListener
      }
      cancelProcessingAndCloseHint()
    }, this)
    IdeEventQueue.getInstance().addActivityListener(Runnable { onActivity() }, this)
    ApplicationManager.getApplication().messageBus.connect(this).subscribe(AnActionListener.TOPIC, MyActionListener())
  }
}
