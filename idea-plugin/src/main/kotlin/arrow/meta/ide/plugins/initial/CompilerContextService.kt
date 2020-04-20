package arrow.meta.ide.plugins.initial

import arrow.meta.phases.CompilerContext
import com.intellij.openapi.project.Project

/**
 * a Compiler Context that is used both in the Ide and Cli.
 * It is first registered in the Ide and as changes are made from both sides they're shared down-stream.
 * So that there is a 1:1 mapping without recreating a CompilerContext.
 * Obtaining the CompilerContext in the Ide is done either through a PsiElement [Project::ctx] or a Project with [Project::ctx]
 */
private class CompilerContextService
private constructor(override val project: Project) : CompilerContext(
  project
)