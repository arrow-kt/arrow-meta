/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package arrow.meta.phases.codegen.ir.interpreter

import arrow.meta.phases.codegen.ir.interpreter.stack.Stack
import arrow.meta.phases.codegen.ir.interpreter.state.Primitive
import arrow.meta.phases.codegen.ir.interpreter.state.isSubtypeOf
import org.jetbrains.kotlin.ir.interpreter.ExecutionResult
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.classifierOrNull
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.render

/**
 * This method is analog of `checkcast` jvm bytecode operation. Throw exception whenever actual type is not a subtype of expected.
 */
internal fun ExecutionResult.implicitCastIfNeeded(expectedType: IrType, actualType: IrType, stack: Stack): ExecutionResult {
    if (actualType.classifierOrNull !is IrTypeParameterSymbol) return this

    if (expectedType.classifierOrFail is IrTypeParameterSymbol) return this

    val actualState = stack.peekReturnValue()
    if (actualState is Primitive<*> && actualState.value == null) return this // this is handled as NullPointerException

    if (!actualState.isSubtypeOf(expectedType)) {
        val convertibleClassName = stack.popReturnValue().irClass.fqNameWhenAvailable
        throw ClassCastException("$convertibleClassName cannot be cast to ${expectedType.render()}")
    }
    return this
}
