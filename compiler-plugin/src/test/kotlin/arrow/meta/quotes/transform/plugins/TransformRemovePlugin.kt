package arrow.meta.quotes.transform.plugins

import arrow.meta.Meta
import arrow.meta.CliPlugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

val Meta.transformRemove: List<CliPlugin>
  get() = listOf(transformRemoveSingleElement, transformRemoveSingleElementFromContext, transformRemoveElementsFromContext)

private val Meta.transformRemoveSingleElement: CliPlugin
  get() =
    "Transform Remove Single Element" {
      meta(
        namedFunction({ name == "transformRemove" }) { f ->
          Transform.remove(f)
        }
      )
    }

private val Meta.transformRemoveSingleElementFromContext: CliPlugin
  get() =
    "Transform Remove Single Element from Context" {
      meta(
        namedFunction({ name == "transformRemoveSingleElement" }) { f ->
          Transform.remove(
            removeIn = f,
            declaration = """ println("") """.expressionIn(f)
          )
        }
      )
    }

private val Meta.transformRemoveElementsFromContext: CliPlugin
  get() =
    "Transform Remove Multiple Elements from Context" {
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