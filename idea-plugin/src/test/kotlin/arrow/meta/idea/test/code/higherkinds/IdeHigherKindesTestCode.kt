package arrow.meta.idea.test.code.higherkinds

object IdeHigherKindesTestCode {

  val code = """
        package test
        import arrow.higherKind
        
        @higherkind
        class Id<out A>(val value: A)
        
        val <caret>x: IdOf<Int> = Id(1)
        """

  val withoutMarkers = """
    package test
    import arrow.higherKind
  
    sealed class Sealed
    @higherKind
    data class <caret>IdSealed<out A>(val number: A) : Sealed()
    
    // missing @higherKind
    class <caret>IdNoHigherKind<out A>(val value: A)
    
    // annotation class
    @higherKind
    annotation class <caret>IdAnnotation<out A>(val value: A)
    
    // missing type parameter
    @higherKind
    annotation class <caret>IdNoTypeParameter(val value: Int)
    
    // missing marker, no type parameter
    class <caret>Outer {
      // nested class
      @higherkind
      class <caret>Nested<out A>(val value: A)
    }
    
    fun foo() {
      // not at top-level
      @higherkind
      class <caret>NotTopLevel<out A>(val value: A) 
    }
  
    // not a class
    val <caret>notAClass: IdOf<Int> = Id(1)
    """.trimIndent()

  val withMarkers = """
    package test
    import arrow.higherKind
    
    @higherkind
    class <caret>Id1<out A>(val value: A)
    
    @arrow.higherkind
    class <caret>Id2<out A>(val value: A)
     
    val x: IdOf<Int> = Id(1)
    """.trimIndent()
}

