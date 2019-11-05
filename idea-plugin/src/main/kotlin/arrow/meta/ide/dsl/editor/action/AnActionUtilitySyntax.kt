package arrow.meta.ide.dsl.editor.action

import arrow.meta.ide.dsl.utils.toNotNullable
import com.intellij.openapi.actionSystem.ActionManager

interface AnActionUtilitySyntax {
  fun collectActionIds(prefix: String): List<String> =
    ActionManager.getInstance().getActionIds(prefix).toList().toNotNullable()
}