package arrow.meta.plugins.liquid.phases.ir

import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.liquid.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.liquid.phases.analysis.solver.collect.constraintsFromSolverState
import arrow.meta.plugins.liquid.smt.fieldNames
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
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.FormulaManager

internal fun IrUtils.annotateWithConstraints(fn: IrFunction) {
  val solverState = compilerContext.get<SolverState>(SolverState.key(moduleFragment.descriptor))
  if (solverState != null) {
    val declarationConstraints = solverState.constraintsFromSolverState(fn.toIrBasedDescriptor())
    if (declarationConstraints != null) {
      solverState.solver.formulae {
        preAnnotation(declarationConstraints.pre, solverState.solver.formulaManager)
          ?.let { fn.addAnnotation(it) }
        postAnnotation(declarationConstraints.post, solverState.solver.formulaManager)
          ?.let { fn.addAnnotation(it) }
        if (fn.hasAnnotation(FqName("arrow.refinement.Law"))) {
          val callToSubject = (fn.body?.statements?.lastOrNull() as? IrReturn)?.value as? IrFunctionAccessExpression
          val fnDescriptor = callToSubject?.symbol?.owner?.toIrBasedDescriptor() as? SimpleFunctionDescriptor
          if (fnDescriptor != null) {
            lawSubjectAnnotation(fnDescriptor)?.let { fn.addAnnotation(it) }
          }
        }
      }
    }
  }
}

private fun IrMutableAnnotationContainer.addAnnotation(annotation: IrConstructorCall) {
  this.annotations = this.annotations + listOf(annotation)
}

private fun IrUtils.preAnnotation(formulae: List<NamedConstraint>, manager: FormulaManager): IrConstructorCall? =
  annotationFromClassId(ClassId.fromString("arrow/refinement/Pre"),
    formulae.map { it.msg },
    formulae.map { it.formula.toString() },
    formulae.flatMap { manager.fieldNames(it.formula).map { it.first }.toSet() })

private fun IrUtils.postAnnotation(formulae: List<NamedConstraint>, manager: FormulaManager): IrConstructorCall? =
  annotationFromClassId(ClassId.fromString("arrow/refinement/Post"),
    formulae.map { it.msg },
    formulae.map { it.formula.toString() },
    formulae.flatMap { manager.fieldNames(it.formula).map { it.first }.toSet() })

private fun IrUtils.lawSubjectAnnotation(descriptor: SimpleFunctionDescriptor): IrConstructorCall? =
  lawSubjectAnnotationFromClassId(ClassId.fromString("arrow/refinement/Subject"), descriptor)

private fun IrUtils.annotationFromClassId(
  classId: ClassId,
  messages: List<String>,
  formulae: List<String>,
  dependencies: List<String>
): IrConstructorCall? =
  when {
    formulae.isEmpty() -> null
    else -> moduleFragment.descriptor.findClassAcrossModuleDependencies(classId)?.let {
      annotation(messages, formulae, dependencies, it)
    }
  }

private fun IrUtils.lawSubjectAnnotationFromClassId(classId: ClassId, descriptor: SimpleFunctionDescriptor): IrConstructorCall? =
  moduleFragment.descriptor.findClassAcrossModuleDependencies(classId)?.let {
    lawSubjectAnnotation(descriptor, it)
  }

private fun IrUtils.lawSubjectAnnotation(fnDescriptor: SimpleFunctionDescriptor, descriptor: ClassDescriptor): IrConstructorCall? =
  descriptor.irConstructorCall()?.also {
    it.putValueArgument(0, constantValue(fnDescriptor.fqNameSafe.asString()))
  }

private fun IrUtils.annotation(messages: List<String>, formulae: List<String>, dependencies: List<String>, descriptor: ClassDescriptor): IrConstructorCall? =
  descriptor.irConstructorCall()?.also {
    it.putValueArgument(0, arrayOfStrings(messages))
    it.putValueArgument(1, arrayOfStrings(formulae))
    it.putValueArgument(2, arrayOfStrings(dependencies))
  }

private fun IrUtils.arrayOfStrings(values: List<String>): IrVarargImpl? =
  moduleFragment.descriptor.getPackage(FqName("kotlin")).memberScope
    .getContributedDescriptors { it.asString() == "arrayOf" }
    .filterIsInstance<SimpleFunctionDescriptor>().firstOrNull()?.let {
      IrVarargImpl(
        startOffset = UNDEFINED_OFFSET,
        endOffset = UNDEFINED_OFFSET,
        type = it.returnType?.toIrType()!!,
        varargElementType = moduleFragment.irBuiltins.stringType,
        elements = values.map(this::constantValue)
      )
    }

private fun IrUtils.constantValue(value: String): IrConst<String> =
  IrConstImpl(
    startOffset = UNDEFINED_OFFSET,
    endOffset = UNDEFINED_OFFSET,
    type = moduleFragment.irBuiltins.stringType,
    kind = IrConstKind.String,
    value = value
  )
