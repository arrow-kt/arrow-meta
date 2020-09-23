package arrow.meta.plugins.union

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class UnionCommutativeTest {
  
  @Test
  fun `test commutative with 2 arities`() =
    validateCommutativeWithTypes("String", "Int")
  
  @Test
  fun `test commutative with 3 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double")
  
  @Test
  fun `test commutative with 4 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float")
    
  @Test
  fun `test commutative with 5 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String")
    
  @Test
  fun `test commutative with 6 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int")
    
  @Test
  fun `test commutative with 7 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double")
    
  @Test
  fun `test commutative with 8 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float")
    
  @Test
  fun `test commutative with 9 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String")
    
  @Test
  fun `test commutative with 10 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int")
    
  @Test
  fun `test commutative with 11 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double")
    
  @Test
  fun `test commutative with 12 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float")
    
  @Test
  fun `test commutative with 13 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String")
    
  @Test
  fun `test commutative with 14 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int")
    
  @Test
  fun `test commutative with 15 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double")
    
  @Test
  fun `test commutative with 16 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float")
    
  @Test
  fun `test commutative with 17 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String")
    
  @Test
  fun `test commutative with 18 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int")
    
  @Test
  fun `test commutative with 19 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double")
    
  @Test
  fun `test commutative with 20 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float")
    
  @Test
  fun `test commutative with 21 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String")
    
  @Test
  fun `test commutative with 22 arities`() =
    validateCommutativeWithTypes("String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int", "Double", "Float", "String", "Int")
    
  private fun validateCommutativeWithTypes(vararg types: String) = assertThis(CompilerTest(
    config = { metaDependencies },
    code = {
      """|import arrow.*
         |
         |val f: Union${types.size}<${types.reduce { acc, type -> "$acc, $type" }}> = 0
         |val x: Union${types.size}<${types.reduceRight { type, acc -> "$acc, $type" }}> = f
         |val y: Int? = x
         |""".source
    },
    assert = {
      allOf("y".source.evalsTo(0))
    }
  ))
}