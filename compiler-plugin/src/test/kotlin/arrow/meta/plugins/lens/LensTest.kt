package arrow.meta.plugins.lens

import arrow.meta.plugin.testing.CompilationTest
import org.junit.Test

class LensTest : CompilationTest {

    @Test
    fun `initial_test`() {

        """
    | 
    |//metadebug
    | 
    |data class TestLenses(
    |   val a: String,
    |   val b: String
    |)
    | 
    """ withDependencies listOf("arrow-annotations:rr-meta-prototype-integration-SNAPSHOT", "arrow-optics:0.10.1") compilesTo """
        |data class TestLenses public constructor (val a: String, val b: String) {
        |
        |    companion object {
        |        @arrow.synthetic val a: arrow.optics.Lens<TestLenses, String> = arrow.optics.Lens(
        |            get = { testlenses -> testlenses.a },
        |            set = { testlenses, a -> testlenses.copy(a = a) }
        |        )
        |        @arrow.synthetic val b: arrow.optics.Lens<TestLenses, String> = arrow.optics.Lens(
        |            get = { testlenses -> testlenses.b },
        |            set = { testlenses, b -> testlenses.copy(b = b) }
        |        )
        |        @arrow.synthetic val iso: arrow.optics.Iso<TestLenses, Pair<String, String>> = arrow.optics.Iso(
        |            get = { (a, b) -> Pair(a, b) },
        |            reverseGet = { (a, b) -> TestLenses(a, b) }
        |        )
        |    }
        |}
"""
    }

}

