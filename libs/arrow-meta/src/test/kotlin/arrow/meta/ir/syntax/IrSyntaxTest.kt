package arrow.meta.ir.syntax

import arrow.meta.ir.plugin.IrSyntaxPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.assertThis
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrTypeAlias
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlock
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrBreak
import org.jetbrains.kotlin.ir.expressions.IrBreakContinue
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrCallableReference
import org.jetbrains.kotlin.ir.expressions.IrCatch
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrContinue
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrElseBranch
import org.jetbrains.kotlin.ir.expressions.IrEnumConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.IrFieldAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetObjectValue
import org.jetbrains.kotlin.ir.expressions.IrGetSingletonValue
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrInstanceInitializerCall
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.expressions.IrSyntheticBody
import org.jetbrains.kotlin.ir.expressions.IrThrow
import org.jetbrains.kotlin.ir.expressions.IrTry
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrValueAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.expressions.IrWhileLoop
import org.junit.jupiter.api.Test

class IrSyntaxTest {

  @Test
  fun `Test Syntax`() {
    testIrVisit(
      listOf(
        IrModuleFragment::class.java,
        IrFile::class.java,
        IrDeclaration::class.java,
        IrClass::class.java,
        IrFunction::class.java,
        IrSimpleFunction::class.java,
        IrConstructor::class.java,
        IrProperty::class.java,
        IrField::class.java,
        // TODO: IrLocalDelegatedProperty::class.java
        IrEnumEntry::class.java,
        // TODO: IrAnonymousInitializer::class.java
        IrVariable::class.java,
        IrTypeParameter::class.java,
        IrValueParameter::class.java,
        IrTypeAlias::class.java,
        IrBody::class.java,
        IrExpressionBody::class.java,
        IrBlockBody::class.java,
        IrSyntheticBody::class.java,
        // TODO: IrSuspendableExpression::class.java
        // TODO: IrSuspensionPoint::class.java
        IrExpression::class.java,
        IrConst::class.java,
        // TODO: IrVararg::class.java,
        // TODO: IrSpreadElement::class.java,
        IrContainerExpression::class.java,
        IrBlock::class.java,
        // TODO: IrComposite::class.java
        IrStringConcatenation::class.java,
        IrDeclarationReference::class.java,
        IrGetSingletonValue::class.java,
        IrGetObjectValue::class.java,
        // TODO: IrGetEnumValue::class.java
        IrValueAccessExpression::class.java,
        IrGetValue::class.java,
        // TODO: IrSetVariable::class.java
        IrFieldAccessExpression::class.java,
        IrGetField::class.java,
        // TODO: IrSetField::class.java
        IrMemberAccessExpression::class.java,
        IrFunctionAccessExpression::class.java,
        IrCall::class.java,
        IrConstructorCall::class.java,
        IrDelegatingConstructorCall::class.java,
        IrEnumConstructorCall::class.java,
        // TODO: IrGetClass::class.java
        IrCallableReference::class.java,
        IrFunctionReference::class.java,
        // TODO: IrPropertyReference::class.java
        // TODO: IrLocalDelegatedPropertyReference::class.java
        // TODO: IrClassReference::class.java
        IrInstanceInitializerCall::class.java,
        IrTypeOperatorCall::class.java,
        IrWhen::class.java,
        IrBranch::class.java,
        IrElseBranch::class.java,
        IrLoop::class.java,
        IrWhileLoop::class.java,
        // TODO: IrDoWhileLoop::class.java,
        IrTry::class.java,
        IrCatch::class.java,
        IrBreakContinue::class.java,
        IrBreak::class.java,
        IrContinue::class.java,
        IrReturn::class.java,
        IrThrow::class.java,
        // TODO: IrDynamicExpression::class.java,
        // TODO: IrDynamicOperatorExpression::class.java,
        // TODO: IrDynamicMemberExpression::class.java,
        // TODO: IrErrorDeclaration::class.java,
        // TODO: IrErrorExpression::class.java,
        // TODO: IrErrorCallExpression::class.java
        ),
      """
        package test
              
        val zero = 0
        
        sealed class ABC<A> {
          data class A(val a: Int) : ABC<Int>()
          data class B(val b: String = "" + "Hello") : ABC<String>()
          data class C(val c: Long = 44L.and(3L)) : ABC<Long>()
          object D : ABC<Nothing>()
        }

        enum class Position {
          FIRST, SECOND, THIRD
        }

        @Suppress("UNCHECKED_CAST")
        class Box<A>(val a: Int, val b : A, val box: ABC<A> = ABC.D as ABC<A>)

        typealias A<B> = Box<B>

        var h: Int
          get() = 0
          set(value) {
            value * 3
          }

        suspend fun hello(): Unit =
          println("Hello")
          
        fun <A> provider(evidence: A): A =
            evidence

        suspend fun foo(vararg a: Int) {
          hello()
          val f = { a: Int -> 9 - a }
          val boo = try {
            var j = 0
            while (j < 1000) {
              fun s(): Int = 8
              if (j < 9) {
                ::s
                break
              }
              continue
            }
            val d = if (3 > f(2)) 0 else 33
          } catch (e: Exception) {
            println()
            throw e
          } finally {
            9 - 6
          }
        }
    """.trimIndent(
      )
    )
  }
}

internal fun <A : IrElement> visits(element: Class<A>): String = "${element.name} is visited"

private fun <A : IrElement> testIrVisit(elements: List<Class<out A>>, src: String = ""): Unit =
  assertThis(
    CompilerTest(
      config = { metaDependencies + addMetaPlugins(IrSyntaxPlugin()) },
      code = { src.source },
      assert = { allOf(elements.map { element -> failsWith { it.contains(visits(element)) } }) }
    )
  )
