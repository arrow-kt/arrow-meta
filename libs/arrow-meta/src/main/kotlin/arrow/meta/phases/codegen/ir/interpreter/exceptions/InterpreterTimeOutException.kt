/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package arrow.meta.phases.codegen.ir.interpreter.exceptions

import org.jetbrains.kotlin.ir.interpreter.exceptions.InterpreterException

class InterpreterTimeOutException : InterpreterException("Exceeded execution limit of constexpr expression")