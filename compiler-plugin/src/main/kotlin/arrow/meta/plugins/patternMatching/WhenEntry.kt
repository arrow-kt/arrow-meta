package arrow.meta.plugins.patternMatching

import org.jetbrains.kotlin.psi.KtWhenEntry

val KtWhenEntry.desugar: String
  get() = """is ${conditions.first().children.first().children[1].text} -> ${expression?.text}"""
