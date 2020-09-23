package arrow.meta.quotes.transform

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.transform.plugins.TransformMetaPlugin
import org.junit.jupiter.api.Test

class TransformNewSourceTest {
  
  @Test
  fun `validate single extra file is created`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """ class NewSource {} """.source
      },
      assert = { quoteFileMatches("NewSource_Generated.kt",
        """
           package arrow
           class NewSource_Generated {
            fun sayHi() = println("Hi!")
           }
        """.source
      )}
    ))
  }
  
  @Test
  fun `validate multiple extra files are created`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """ class NewMultipleSource {} """.source
      },
      assert = {
        allOf(
          quoteFileMatches("NewMultipleSource_Generated.kt",
            """
               package arrow
               class NewMultipleSource_Generated {
                fun sayHi() = println("Hi!")
               }
            """.source
          ),
          quoteFileMatches("NewMultipleSource_Generated_2.kt",
            """
               package arrow
               class NewMultipleSource_Generated_2 {
                fun say(name: String) = println(name)
               }
            """.source
          )
        )
      }
    ))
  }
  
  @Test
  fun `Check if the file is modified and another file is created`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """
        | //metadebug
        |
        | class NewSourceMany {
        |   fun printFirst() = println("Foo")
        |   fun printSecond() = println("Bar")
        | }
        """.source
      },
      assert = {
        allOf(
          quoteOutputMatches(""" @arrow.synthetic private class NewSourceMany """.source),
          quoteFileMatches("NewSourceMany_Generated.kt",
            """
               package arrow
               class NewSourceMany_Generated {
                 fun sayHello() = println("Hello!")
                 fun say(name: String) = println(name)
               }
            """.source
          )
        )
      }
    ))
  }

  @Test
  fun `validate single extra file is created with custom path`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """ class NewSourceWithCustomPath {} """.source
      },
      assert = { quoteFileMatches( filename = "NewSourceWithCustomPath_Generated.kt",
        source = """
          package arrow
          class NewSourceWithCustomPath_Generated
        """.source,
        sourcePath = "build/generated/source/kapt/test/files"
      )}
    ))
  }

  @Test
  fun `validate multiple extra files are created with custom path`() {
    assertThis(CompilerTest(
      config = { metaDependencies + addMetaPlugins(TransformMetaPlugin()) },
      code = {
        """ class NewMultipleSourceWithCustomPath {} """.source
      },
      assert = {
        allOf(
          quoteFileMatches( filename = "NewMultipleSourceWithCustomPath_Generated.kt",
            source = """
             package arrow
             class NewMultipleSourceWithCustomPath_Generated
            """.source,
            sourcePath = "build/generated/source/kapt/test/files"
          ),
          quoteFileMatches( filename = "NewMultipleSourceWithCustomPath_Generated_2.kt",
            source = """
             package arrow
             class NewMultipleSourceWithCustomPath_Generated_2
            """.source,
            sourcePath = "build/generated/source/kapt/test/files/source"
          )
        )
      }
    ))
  }
}