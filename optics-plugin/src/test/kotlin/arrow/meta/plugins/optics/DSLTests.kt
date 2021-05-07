package arrow.meta.plugins.optics

import org.junit.jupiter.api.Test

class DSLTests {

  @Test
  fun `DSL is generated for complex model`() {
    """
      |$imports
      |$dslModel
      |val john = Employee("Audrey Tang",
      |       Company("Arrow",
      |               Address("Functional city",
      |                       Street(42, "lambda street"))))
      |val modify = Employee.company.address.street.name.modify(john, String::toUpperCase)
      |val r = modify.company?.address?.street?.name
      """ { "r".source.evalsTo("LAMBDA STREET") }
  }

}
