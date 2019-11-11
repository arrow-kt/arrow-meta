package arrow.meta.ide.dsl.editor.inspection

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.idea.util.actualsForExpected
import org.jetbrains.kotlin.idea.util.liftToExpected
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.Check

interface InspectionUtilitySyntax {
  fun KtDeclaration.withExpectedActuals(): List<KtDeclaration> {
    val expect = liftToExpected() ?: return listOf(this)
    val actuals = expect.actualsForExpected()
    return listOf(expect) + actuals
  }

  val ArrowPath: Array<String>
    get() = arrayOf("Kotlin", "Λrrow")
}

sealed class ExtendedReturnsCheck(val name: String, val type: KotlinBuiltIns.() -> KotlinType) : Check {
  override val description = "must return $name"
  override fun check(functionDescriptor: FunctionDescriptor) =
    functionDescriptor.returnType == functionDescriptor.builtIns.type()

  object ReturnsNothing : ExtendedReturnsCheck("Nothing", { nothingType })
  object ReturnsNullableNothing : ExtendedReturnsCheck("NullableNothing", { nullableNothingType })
}