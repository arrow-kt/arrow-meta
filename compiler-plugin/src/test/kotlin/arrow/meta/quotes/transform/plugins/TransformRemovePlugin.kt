package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

val Meta.transformRemove: List<Plugin>
  get() = listOf(transformRemoveSingleElement, transformRemoveSingleElementFromContext, transformRemoveElementsFromContext)

private val Meta.transformRemoveSingleElement: Plugin
  get() =
    "Transform Remove" {
      meta(
        namedFunction({ name == "transformRemove" }) { f ->
          Transform.remove(f)
        }
      )
    }

private val Meta.transformRemoveSingleElementFromContext: Plugin
  get() =
    "Transform Remove" {
      meta(
        namedFunction({ name == "transformRemoveSingleElement" }) { f ->
          Transform.remove(
            removeIn = f,
            declaration = """ println("") """.expressionIn(f)
          )
        }
      )
    }

private val Meta.transformRemoveElementsFromContext: Plugin
  get() =
    "Transform Remove" {
      meta(
        namedFunction({ name == "transformRemoveElements" }) { f ->
          Transform.remove(
            removeIn = f,
            declarations = listOf(
              """ println("") """.expressionIn(f),
              """ println("asd") """.expressionIn(f)
            )
          )
        }
      )
    }