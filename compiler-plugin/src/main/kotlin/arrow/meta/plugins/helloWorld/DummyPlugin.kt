package arrow.meta.plugins.helloWorld

import arrow.meta.Meta
import arrow.meta.Plugin
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.namedFunction

/**
 * The following example shows a Hello World Compiler Plugin.
 *
 * The Hello World plugin auto implements the `helloWorld` function by rewriting the Kotlin AST before the compiler proceeds.
 *
 * ```kotlin
 * val Meta.helloWorld: Plugin get() =
 *   "Hello World" {
 *     meta(
 *       namedFunction({ name == "helloWorld" }) { c ->  // <-- namedFunction(...) {...}
 *         Transform.replace(
 *           replacing = c,
 *           newDeclaration = """|fun helloWorld(): Unit =
 *                               |  println("Hello ΛRROW Meta!")
 *                               |""".function.synthetic
 *         )
 *       }
 *     )
 *   }
 * ```
 *
 * For any user code whose function name is `helloWorld`, our compiler plugin will replace the matching function for a
 * function that returns Unit and prints our message.
 *
 * ```kotlin:diff
 * -fun helloWorld(): Unit = TODO()
 * +fun helloWorld(): Unit =
 * +  println("Hello ΛRROW Meta!")
 * ```
 *
 * The Arrow Meta Compiler Plugin can be enabled in your project with the Arrow Meta Gradle Plugin.
 *
 * It's published in the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.arrow-kt.arrow).
 *
 * It can be used with the plugins DSL for release versions:
 *
 * ```
 * plugins {
 *   id "io.arrow-kt.arrow" version "<release-version>"
 * }
 * ```
 *
 * If using a snapshot version, it must be included with the legacy plugin application:
 *
 * ```
 * buildscript {
 *   repositories {
 *     maven { url "https://oss.jfrog.org/artifactory/oss-snapshot-local/" }
 *   }
 *   dependencies {
 *     classpath "io.arrow-kt:arrow-meta-gradle-plugin:<snapshot-version>"
 *     classpath "io.arrow-kt:arrow-meta-compiler-plugin:<snapshot-version>"
 *   }
 * }
 *
 * apply plugin: "io.arrow-kt.arrow"
 * ```
 *
 * Take a look at the [`arrow-meta-examples`](https://github.com/arrow-kt/arrow-meta-examples) repository for more details.
 */
val Meta.helloWorld: Plugin
  get() =
    "Hello World" {
      meta(
        namedFunction({ name == "helloWorld" }) { c ->
          Transform.replace(
            replacing = c,
            newDeclaration =
            """|fun helloWorld(): Unit = 
               |  println("Hello ΛRROW Meta!")
               |""".function.syntheticScope
          )
        }
      )
    }
