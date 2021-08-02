package arrow.meta.plugins.liquid.phases.ir

import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.liquid.phases.analysis.solver.SolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.constraintsFromSolverState
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrMutableAnnotationContainer
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrVarargElement
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

internal fun IrUtils.annotateWithConstraints(fn: IrFunction): Unit {
  val solverState = compilerContext.get<SolverState>(SolverState.key(moduleFragment.descriptor))
  if (solverState != null) {
    val declarationConstraints = solverState.constraintsFromSolverState(fn.toIrBasedDescriptor())
    if (declarationConstraints != null) {
      solverState.solver.formulae {
        val preFormulae = declarationConstraints.pre.mapNotNull {
          val builder = StringBuilder()
          solverState.solver.formulae {
            dumpFormula(it)
          }.appendTo(builder)
          builder.toString()
        }
        val postFormulae = declarationConstraints.post.mapNotNull {
          val builder = StringBuilder()
          solverState.solver.formulae {
            dumpFormula(it)
          }.appendTo(builder)
          builder.toString()
        }
        if (preFormulae.isNotEmpty()) {
          preAnnotation(preFormulae)?.let { fn.addAnnotation(it) }
        }
        if (postFormulae.isNotEmpty()) {
          postAnnotation(postFormulae)?.let { fn.addAnnotation(it) }
        }
      }
    }
  }
}


private fun IrMutableAnnotationContainer.addAnnotation(annotation: IrConstructorCall): Unit {
  this.annotations = this.annotations + listOf(annotation)
}

private fun IrUtils.preAnnotation(formulae: List<String>): IrConstructorCall? =
  annotationFromClassId(ClassId.fromString("arrow/refinement/Pre"), formulae)

private fun IrUtils.postAnnotation(formulae: List<String>): IrConstructorCall? =
  annotationFromClassId(ClassId.fromString("arrow/refinement/Post"), formulae)

private fun IrUtils.annotationFromClassId(classId: ClassId, formulae: List<String>): IrConstructorCall? =
  moduleFragment.descriptor.findClassAcrossModuleDependencies(classId)?.let {
    annotation(formulae, it)
  }

private fun IrUtils.annotation(formulae: List<String>, descriptor: ClassDescriptor): IrConstructorCall? =
  descriptor.irConstructorCall()?.also {
    val arrayOfCns = moduleFragment.descriptor.getPackage(FqName("kotlin")).memberScope.getContributedDescriptors { it.asString() == "arrayOf" }.filterIsInstance<SimpleFunctionDescriptor>().firstOrNull()
    if (arrayOfCns != null) {
      it.putValueArgument(0, IrVarargImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = arrayOfCns.returnType?.toIrType()!!,
        varargElementType = moduleFragment.irBuiltins.stringType,
        elements = formulae.map {
          constantValue(it)
        }
      ))
    }
  }

private fun IrUtils.constantValue(value: String): IrConst<String> =
  IrConstImpl(
    startOffset = UNDEFINED_OFFSET,
    endOffset = UNDEFINED_OFFSET,
    type = moduleFragment.irBuiltins.stringType,
    kind = IrConstKind.String,
    value = value
  )