package arrow.meta.dsl.platform

import org.jetbrains.kotlin.com.intellij.openapi.project.Project

private val isIde: Boolean = Project::class.java.name == "com.intellij.openapi.project.Project"

private val isCli: Boolean =
  !isIde && Project::class.java.name == "org.jetbrains.kotlin.com.intellij.openapi.project.Project"

/**
 * The [cli] function selectively evaluates [f] in the Command Line Compiler. [f] is ignored if this
 * is an IDEA plugin
 */
fun <A> cli(f: () -> A): A? = if (isCli) f() else null

/**
 * The [ide] function selectively evaluates [f] in the IDEA plugin. [f] is ignored if this is an
 * command line compiler plugin
 */
fun <A> ide(f: () -> A): A? = if (isIde) f() else null
