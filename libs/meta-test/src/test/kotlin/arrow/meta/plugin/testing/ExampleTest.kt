package arrow.meta.plugin.testing

import arrow.meta.plugin.testing.Code.Source
import arrow.meta.plugin.testing.plugins.MetaPlugin
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class ExampleTest {

  @Test
  fun `checks that compiles`() {
    assertThis(
      CompilerTest(
        code = {
          """
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
        },
        assert = { compiles }
      )
    )
  }

  @Test
  fun `check an expression evaluation`() {
    assertThis(
      CompilerTest(
        code = {
          """
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
        },
        assert = { "hello()".source.evalsTo("Hello world!") }
      )
    )
  }

  @Test
  fun `check an expression evaluation in a package`() {
    assertThis(
      CompilerTest(
        code = {
          """
        | package io.arrow.kt
        | 
        | fun hello(): String =
        |   "Hello world!"
        | 
        """.source
        },
        assert = { "hello()".source.evalsTo("Hello world!") }
      )
    )
  }

  @Test
  fun `check that fails`() {
    assertThis(
      CompilerTest(
        code = { """
        | classsss Error
        | 
        """.source },
        assert = { fails }
      )
    )
  }

  @Test
  fun `check that emits an error diagnostic when compilation fails`() {
    assertThis(
      CompilerTest(
        code = { """
        | classsss Error
        | 
        """.source },
        assert = { failsWith { it.contains("Expecting a top level declaration") } }
      )
    )
  }

  @Test
  fun `eval a variable`() {
    assertThis(
      CompilerTest(
        code = {
          """
        | 
        | val x: String = "Hello world!"
        | 
        """.source
        },
        assert = { "x".source.evalsTo("Hello world!") }
      )
    )
  }

  @Test
  fun `eval a variable in a package`() {
    assertThis(
      CompilerTest(
        code = {
          """
        | package test
        | 
        | val x: String = "Hello world!"
        | 
        """.source
        },
        assert = { "x".source.evalsTo("Hello world!") }
      )
    )
  }

  @Test
  fun `allows several sources with names to add associated asserts`() {
    assertThis(
      CompilerTest(
        code = {
          sources(
            Source(
              filename = "LowerCase.kt",
              text =
                """
              |
              | val x: String = "hello world!"
              | 
              """
            ),
            Source(
              filename = "UpperCase.kt",
              text =
                """
              |
              | val y: String = "HELLO WORLD!"
              | 
              """
            )
          )
        },
        assert = {
          allOf(
            Source(filename = "LowerCaseKt", text = "x").evalsTo("hello world!"),
            Source(filename = "UpperCaseKt", text = "y").evalsTo("HELLO WORLD!")
          )
        }
      )
    )
  }

  @Test
  fun `allows several sources without names because they don't matter`() {
    assertThis(
      CompilerTest(
        code = {
          sources(
            """
          |
          | val x: String = "hello world!"
          | 
          """.source,
            """
          |
          | val y: String = "HELLO WORLD!"
          | 
          """.source
          )
        },
        assert = { compiles }
      )
    )
  }

  @Test
  @Disabled
  fun `allows to test a Meta plugin`() {
    assertThis(
      CompilerTest(
        config = { metaDependencies + addMetaPlugins(MetaPlugin()) },
        code = {
          """
          | //metadebug
          | 
          | fun helloWorld(): String = TODO()
          | 
          """.source
        },
        assert = { "helloWorld()".source.evalsTo("Hello ΛRROW Meta!") }
      )
    )
  }

  @Test
  fun `allows to pass compilation arguments`() {
    assertThis(
      CompilerTest(
        config = { listOf(addArguments("-Xallow-jvm-ir-dependencies")) },
        code = {
          """
          | 
          | fun helloWorld(): String = "Hello World!"
          | 
          """.source
        },
        assert = { compiles }
      )
    )
  }

  @Test
  fun `allows to pass command line processors and plugin options`() {
    assertThis(
      CompilerTest(
        config = {
          listOf(
            addCommandLineProcessors(ExampleCommandLineProcessor()),
            addPluginOptions(
              PluginOption(
                ExampleCommandLineProcessor().pluginId,
                ExampleCommandLineProcessor.CLI_OPTION.optionName,
                "value"
              )
            )
          )
        },
        code = {
          """
          | 
          | fun helloWorld(): String = "Hello World!"
          | 
          """.source
        },
        assert = { compiles }
      )
    )
  }

  @Test
  fun `fails when providing plugin options without the corresponding command line processor`() {
    assertThis(
      CompilerTest(
        config = { listOf(addPluginOptions(PluginOption("plugin.id", "key", "value"))) },
        code = {
          """
          | 
          | fun helloWorld(): String = "Hello World!"
          | 
          """.source
        },
        assert = {
          failsWith {
            it.contains("Unsupported plugin option") && it.contains("plugin.id:key=value")
          }
        }
      )
    )
  }

  @Test
  fun `fails when providing a wrong plugin option for a command line processor`() {
    assertThis(
      CompilerTest(
        config = {
          listOf(
            addCommandLineProcessors(ExampleCommandLineProcessor()),
            addPluginOptions(
              PluginOption(ExampleCommandLineProcessor().pluginId, "wrongKey", "value")
            )
          )
        },
        code = {
          """
          | 
          | fun helloWorld(): String = "Hello World!"
          | 
          """.source
        },
        assert = {
          failsWith {
            it.contains("Unsupported plugin option") &&
              it.contains("${ExampleCommandLineProcessor().pluginId}:wrongKey=value")
          }
        }
      )
    )
  }
}
