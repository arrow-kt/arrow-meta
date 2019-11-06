package arrow.meta.plugins.helloWorld

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classOrObject
import arrow.meta.quotes.func

//val Meta.helloWorld: Plugin
//  get() =
//    "Hello World" {
//      meta(
//        func({ name == "helloWorld" }) { c ->
//          Transform.replace(
//            replacing = c,
//            newDeclaration =
//            """|fun helloWorld(): Unit =
//               |  println("Hello ΛRROW Meta!")
//               |""".function.synthetic
//          )
//        }
//      )
//    }

val Meta.example: Plugin
  get() =
    "Example" {
      meta(
        classOrObject({ name == "Test" }) {
          Transform.replace(it,
            """|$`@annotationEntries` $kind $name $`(typeParameters)` $`(valueParameters)` : $supertypes"} {
               |  $body
               |  fun void test(): Unit =
               |    println("Implemented by ΛRROW Meta!")
               |}
               |""".`class`.synthetic
          )
        }
      )
    }
