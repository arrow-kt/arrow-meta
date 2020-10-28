package arrow.meta.quotes.transform

import arrow.meta.MetaCliProcessor
import arrow.meta.phases.analysis.DefaultElementScope.Companion.DEFAULT_BASE_DIR
import arrow.meta.phases.analysis.DefaultElementScope.Companion.DEFAULT_GENERATED_SRC_PATH
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.PluginOption
import arrow.meta.plugin.testing.assertThis
import arrow.meta.quotes.transform.plugins.TransformMetaPlugin
import org.junit.jupiter.api.Test
import java.nio.file.Path
import java.nio.file.Paths

class TransformNewSourceTest {

  companion object {
    val CUSTOM_BASE_DIR: Path = Paths.get("build", "test")

    val CUSTOM_GENERATED_SRC_PATH_1: Path = Paths.get("generated", "source", "kapt", "test", "files")
    val CUSTOM_GENERATED_SRC_PATH_2: Path = Paths.get("generated", "source", "kapt", "test", "files", "source")
  }

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
        sourcePath = DEFAULT_BASE_DIR.resolve(CUSTOM_GENERATED_SRC_PATH_1)
      )}
    ))
  }

  @Test
  fun `validate single extra file is created with base directory`() {
    assertThis(CompilerTest(
      config = { metaDependencies +
        addMetaPlugins(TransformMetaPlugin()) +
        addCommandLineProcessors(MetaCliProcessor()) +
        addPluginOptions(
          PluginOption(MetaCliProcessor().pluginId, MetaCliProcessor.ARROW_META_GENERATED_SRC_OUTPUT_DIR.optionName, CUSTOM_BASE_DIR.toString()))
      },
      code = {
        """ class NewSourceWithBaseDir {} """.source
      },
      assert = { quoteFileMatches( filename = "NewSourceWithBaseDir_Generated.kt",
        source = """
          package arrow
          class NewSourceWithBaseDir_Generated
        """.source,
        sourcePath = CUSTOM_BASE_DIR.resolve(DEFAULT_GENERATED_SRC_PATH)
      )}
    ))
  }

  @Test
  fun `validate single extra file is created with base directory and custom path`() {
    assertThis(CompilerTest(
      config = { metaDependencies +
        addMetaPlugins(TransformMetaPlugin()) +
        addCommandLineProcessors(MetaCliProcessor()) +
        addPluginOptions(
          PluginOption(MetaCliProcessor().pluginId, MetaCliProcessor.ARROW_META_GENERATED_SRC_OUTPUT_DIR.optionName, CUSTOM_BASE_DIR.toString()))
      },
      code = {
        """ class NewSourceWithBaseDirAndCustomPath {} """.source
      },
      assert = { quoteFileMatches( filename = "NewSourceWithBaseDirAndCustomPath_Generated.kt",
        source = """
          package arrow
          class NewSourceWithBaseDirAndCustomPath_Generated
        """.source,
        sourcePath = CUSTOM_BASE_DIR.resolve(CUSTOM_GENERATED_SRC_PATH_1)
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
            sourcePath = DEFAULT_BASE_DIR.resolve(CUSTOM_GENERATED_SRC_PATH_1)
          ),
          quoteFileMatches( filename = "NewMultipleSourceWithCustomPath_Generated_2.kt",
            source = """
             package arrow
             class NewMultipleSourceWithCustomPath_Generated_2
            """.source,
            sourcePath = DEFAULT_BASE_DIR.resolve(CUSTOM_GENERATED_SRC_PATH_2)
          )
        )
      }
    ))
  }

  @Test
  fun `validate multiple extra files are created with base directory`() {
    assertThis(CompilerTest(
      config = { metaDependencies +
        addMetaPlugins(TransformMetaPlugin()) +
        addCommandLineProcessors(MetaCliProcessor()) +
        addPluginOptions(
          PluginOption(MetaCliProcessor().pluginId, MetaCliProcessor.ARROW_META_GENERATED_SRC_OUTPUT_DIR.optionName, CUSTOM_BASE_DIR.toString()))
      },
      code = {
        """ class NewMultipleSourceWithBaseDir {} """.source
      },
      assert = {
        allOf(
          quoteFileMatches( filename = "NewMultipleSourceWithBaseDir_Generated.kt",
            source = """
             package arrow
             class NewMultipleSourceWithBaseDir_Generated
            """.source,
            sourcePath = CUSTOM_BASE_DIR.resolve(DEFAULT_GENERATED_SRC_PATH)
          ),
          quoteFileMatches( filename = "NewMultipleSourceWithBaseDir_Generated_2.kt",
            source = """
             package arrow
             class NewMultipleSourceWithBaseDir_Generated_2
            """.source,
            sourcePath = CUSTOM_BASE_DIR.resolve(DEFAULT_GENERATED_SRC_PATH)
          )
        )
      }
    ))
  }

  @Test
  fun `validate multiple extra files are created with base directory and custom path`() {
    assertThis(CompilerTest(
      config = { metaDependencies +
        addMetaPlugins(TransformMetaPlugin()) +
        addCommandLineProcessors(MetaCliProcessor()) +
        addPluginOptions(
          PluginOption(MetaCliProcessor().pluginId, MetaCliProcessor.ARROW_META_GENERATED_SRC_OUTPUT_DIR.optionName, CUSTOM_BASE_DIR.toString()))
      },
      code = {
        """ class NewMultipleSourceWithBaseDirAndCustomPath {} """.source
      },
      assert = {
        allOf(
          quoteFileMatches( filename = "NewMultipleSourceWithBaseDirAndCustomPath_Generated.kt",
            source = """
             package arrow
             class NewMultipleSourceWithBaseDirAndCustomPath_Generated
            """.source,
            sourcePath = CUSTOM_BASE_DIR.resolve(CUSTOM_GENERATED_SRC_PATH_1)
          ),
          quoteFileMatches( filename = "NewMultipleSourceWithBaseDirAndCustomPath_Generated_2.kt",
            source = """
             package arrow
             class NewMultipleSourceWithBaseDirAndCustomPath_Generated_2
            """.source,
            sourcePath = CUSTOM_BASE_DIR.resolve(CUSTOM_GENERATED_SRC_PATH_2)
          )
        )
      }
    ))
  }
}