package arrow.meta.ide.dsl.editor.fileEditor

import arrow.meta.ide.MetaIde
import arrow.meta.ide.phases.editor.fileEditor.EditorProvider
import arrow.meta.internal.Noop
import arrow.meta.phases.ExtensionPhase
import com.intellij.codeHighlighting.TextEditorHighlightingPass
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactory
import com.intellij.codeHighlighting.TextEditorHighlightingPassFactoryRegistrar
import com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

interface EditorSyntax {

  /**
   * Registers a [CaretListener] for each editor
   */
  fun MetaIde.addCaretListener(
    caretAdded: Editor.(document: Document, event: CaretEvent) -> Unit = Noop.effect3,
    caretPositionChanged: Editor.(document: Document, event: CaretEvent) -> Unit = Noop.effect3,
    caretRemoved: Editor.(document: Document, event: CaretEvent) -> Unit = Noop.effect3
  ): ExtensionPhase = addFileEditorListener(
    fileOpened = { _: FileEditorManager, _: VirtualFile, _: FileEditor, document: Document ->
      EditorFactory.getInstance().getEditors(document).mapNotNull { editor ->
        editor.caretModel.addCaretListener(
          caretListener(
            { caretAdded(editor, document, it) },
            { caretPositionChanged(editor, document, it) },
            { caretRemoved(editor, document, it) })
        )
      }
    }
  )

  /**
   * Registers a [FileEditorManagerListener]
   */
  fun MetaIde.addFileEditorListener(
    selectionChanged: (event: FileEditorManagerEvent) -> Unit = Noop.effect1,
    fileOpened: (manager: FileEditorManager, file: VirtualFile, fileEditor: FileEditor, document: Document) -> Unit = Noop.effect4,
    fileOpenedSync: (source: FileEditorManager, file: VirtualFile, editors: Pair<List<FileEditor>, List<FileEditorProvider>>) -> Unit = Noop.effect3,
    fileClosed: (source: FileEditorManager, file: VirtualFile) -> Unit = Noop.effect2
  ): ExtensionPhase =
    EditorProvider.FileEditorListener(fileEditorListener(selectionChanged, fileOpened, fileOpenedSync, fileClosed))

  fun EditorSyntax.fileEditorListener(
    selectionChanged: (event: FileEditorManagerEvent) -> Unit = Noop.effect1,
    fileOpened: (manager: FileEditorManager, file: VirtualFile, fileEditor: FileEditor, document: Document) -> Unit = Noop.effect4,
    fileOpenedSync: (source: FileEditorManager, file: VirtualFile, editors: Pair<List<FileEditor>, List<FileEditorProvider>>) -> Unit = Noop.effect3,
    fileClosed: (source: FileEditorManager, file: VirtualFile) -> Unit = Noop.effect2
  ): FileEditorManagerListener =
    object : FileEditorManagerListener {
      override fun selectionChanged(event: FileEditorManagerEvent) =
        selectionChanged(event)

      override fun fileOpened(source: FileEditorManager, file: VirtualFile) =
        source.getSelectedEditor(file)?.let { fileEditor ->
          FileDocumentManager.getInstance().getDocument(file)?.let { document ->
            fileOpened(source, file, fileEditor, document)
          }
        } ?: Unit

      override fun fileOpenedSync(source: FileEditorManager, file: VirtualFile, editors: Pair<Array<FileEditor>, Array<FileEditorProvider>>) =
        fileOpenedSync(source, file, Pair(editors.first.toList(), editors.second.toList()))

      override fun fileClosed(source: FileEditorManager, file: VirtualFile) =
        fileClosed(source, file)
    }

  fun EditorSyntax.caretListener(
    caretAdded: (event: CaretEvent) -> Unit = Noop.effect1,
    caretPositionChanged: (event: CaretEvent) -> Unit = Noop.effect1,
    caretRemoved: (event: CaretEvent) -> Unit = Noop.effect1
  ): CaretListener =
    object : CaretListener {
      override fun caretAdded(event: CaretEvent) =
        caretAdded(event)

      override fun caretPositionChanged(event: CaretEvent) =
        caretPositionChanged(event)

      override fun caretRemoved(event: CaretEvent) =
        caretRemoved(event)
    }

  fun MetaIde.textEditorHighlighting(
    factoryRegistrar: TextEditorHighlightingPassFactoryRegistrar
  ): ExtensionPhase =
    addPMListener(opened = { project ->
      TextEditorHighlightingPassRegistrar.getInstance(project)?.run {
        // registration method in com.intellij.codeInsight.daemon.impl.TextEditorHighlightingPassRegistrarImpl.TextEditorHighlightingPassRegistrarImpl
        // clear room of improvement, but leaving it as is for backward compatibility
        factoryRegistrar.registerHighlightingPassFactory(this, project)
      }
    })

  fun MetaIde.addTextEditorHighlighting(
    factory: TextEditorHighlightingPassRegistrar.(project: Project) -> Unit = Noop.effect2,
    highlightingPass: Editor.(file: PsiFile) -> TextEditorHighlightingPass? = Noop.nullable2()
  ): ExtensionPhase =
    textEditorHighlighting(
      // TODO: Extend it with TextEditorHighlightingPass, com.intellij.codeHighlighting.MainHighlightingPassFactory, etc.
      object : TextEditorHighlightingPassFactoryRegistrar, TextEditorHighlightingPassFactory {
        override fun registerHighlightingPassFactory(registrar: TextEditorHighlightingPassRegistrar, project: Project): Unit =
          factory(registrar, project)

        // uses casts in the internals to call this check TextEditorHighlightingPassRegistrarImpl and calls from com.intellij.codeHighlighting.TextEditorHighlightingPassRegistrar
        override fun createHighlightingPass(file: PsiFile, editor: Editor): TextEditorHighlightingPass? =
          highlightingPass(editor, file)
      }
    )
}