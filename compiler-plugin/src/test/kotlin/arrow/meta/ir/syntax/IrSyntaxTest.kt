package arrow.meta.ir.syntax

import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.junit.jupiter.api.Test

class IrSyntaxTest {

  @Test
  fun `Visits irProperty`() {
    testIrVisit(IrProperty::class.java)
  }

  @Test
  fun `Visits irModuleFragment`() {
    testIrVisit(IrModuleFragment::class.java)
  }
}