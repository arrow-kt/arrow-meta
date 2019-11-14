package arrow.meta.ide.plugins.comprehensions

object ComprehensionsTestCode {
  private val IO_CLASS_4_TESTS = """
       import kotlin.reflect.KProperty
      
       //metadebug
      
       class IO<A>(val value: A) {
      
         operator fun getValue(value: Any?, property: KProperty<*>): A = TODO()
      
         fun <B> flatMap(f: (A) -> IO<B>): IO<B> =
           f(value)
      
         companion object {
           fun <A> fx(f: IO.Companion.() -> A): IO<A> = TODO()
           fun <A> just(a: A): IO<A> = IO(a)
         }
       }
      """.trimIndent()

  val code1 = """
      $IO_CLASS_4_TESTS
      
       fun test(): IO<Int> =
         IO.fx {
           val a: Int by IO(1)
           val b: Int by IO(2)
           a + b
         }
         
      """.trimIndent()

  val code2 = """
      $IO_CLASS_4_TESTS
      
       fun test(): IO<Int> =
         IO.fx {
           val a by IO(1)
           val b by IO(2)
           a + b
         }
      """.trimIndent()

  val code3 = """
    $IO_CLASS_4_TESTS
    
     fun test(): IO<Int> =
       IO.fx {
         val a by IO.fx {
           val a by IO(1)
           val b by IO(2)
           a + b
         }
         val b by IO.fx {
           val a by IO(3)
           val b by IO(4)
           a + b
         }
         a + b
       }
    """.trimIndent()

  val code4 =
    """
      $IO_CLASS_4_TESTS
      
       fun test(): IO<Int> =
         IO.fx {
           val a by IO(1)
           val t = a + 1
           val b by IO(2)
           val y = a + b
           val f by IO(3)
           val n = a + 1
           val g by IO(4)
           y + f + g + t + n
         }
         
      """

  val code5 = """
      $IO_CLASS_4_TESTS
      
       fun test(): IO<Int> =
         IO.fx { 1 + 1 }
      
      """

  val code6 = """
        $IO_CLASS_4_TESTS
        
         fun test(): IO<Int> =
           IO.fx { a + 1 }
        
        """// 0
}