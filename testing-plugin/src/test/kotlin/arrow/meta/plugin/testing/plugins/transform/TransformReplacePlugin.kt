package arrow.meta.plugin.testing.plugins.transform

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

val Meta.transformReplace: Plugin
	get() = "Transform Replace" {
		meta(
			namedFunction({ name == "transformReplace" }) { c ->
				Transform.replace(
				  c,
					""" fun transformReplace() = println("Transform Replace") """.function.synthetic
				)
			}
		)
	}