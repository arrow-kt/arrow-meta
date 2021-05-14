package arrow

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll

class ExampleJvmTest: StringSpec({
  "String size with forAll" {
    forAll<String, String> { a, b ->
      (a + b).length == a.length + b.length
    }
  }
})