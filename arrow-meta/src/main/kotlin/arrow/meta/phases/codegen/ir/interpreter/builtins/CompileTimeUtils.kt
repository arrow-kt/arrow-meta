/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package arrow.meta.phases.codegen.ir.interpreter.builtins

import org.jetbrains.kotlin.name.FqName

val compileTimeAnnotation = FqName("kotlin.CompileTimeCalculation")
val evaluateIntrinsicAnnotation = FqName("kotlin.EvaluateIntrinsic")
val contractsDslAnnotation = FqName("kotlin.internal.ContractsDsl")

//data class CompileTimeFunction(val methodName: String, val args: List<String>)
