package arrow.meta.ide.plugins.higherkinds

internal object HigherKindsTestCode {

  val code1 = """
        package test
        import arrow.higherKind
        
        @higherkind
        class Id<out A>(val value: A)
        
        val x: IdOf<Int> = Id(1)
        """

  val code2 = """
    package test
    import arrow.higherKind
    
    @higherkind
    class Id1<out A>(val value: A)
    
    @arrow.higherkind
    class Id2<out A>(val value: A)
     
    val x: IdOf<Int> = Id(1)
    """.trimIndent()

  val code3 = """
    package test
    import arrow.higherKind
  
    sealed class Sealed
    @higherKind
    data class IdSealed<out A>(val number: A) : Sealed()
    
    // missing @higherKind
    class IdNoHigherKind<out A>(val value: A)
    
    // annotation class
    @higherKind
    annotation class IdAnnotation<out A>(val value: A)
    
    // missing type parameter
    @arrow.higherKind
    annotation class IdNoTypeParameter(val value: Int)
    
    // missing marker, no type parameter
    class Outer {
      // nested class
      @higherkind
      class Nested<out A>(val value: A)
    }
    
    fun foo() {
      // not at top-level
      @higherkind
      class NotTopLevel<out A>(val value: A) 
    }
  
    // not a class
    val notAClass: IdOf<Int> = Id(1)
    """.trimIndent()
}

