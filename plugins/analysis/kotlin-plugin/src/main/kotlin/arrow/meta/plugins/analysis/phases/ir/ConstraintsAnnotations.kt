package arrow.meta.plugins.analysis.phases.ir

import arrow.meta.phases.codegen.ir.IrUtils
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.FunctionDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.withAliasUnwrapped
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.constraintsFromSolverState
import arrow.meta.plugins.analysis.phases.analysis.solver.isALaw
import arrow.meta.plugins.analysis.smt.fieldNames
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrMutableAnnotationContainer
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstKind
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.sosy_lab.java_smt.api.FormulaManager

internal fun IrUtils.annotateWithConstraints(fn: IrFunction) {
  val kotlinModule: ModuleDescriptor = moduleFragment.descriptor.model()
  val solverState = compilerContext.get<SolverState>(SolverState.key(kotlinModule))
  if (solverState != null && !solverState.hadParseErrors()) {
    val model = fn.toIrBasedDescriptor()
      .model<org.jetbrains.kotlin.descriptors.FunctionDescriptor, FunctionDescriptor>()
    val declarationConstraints = solverState.constraintsFromSolverState(model)
    if (declarationConstraints != null) {
      solverState.solver.formulae {
        preAnnotation(declarationConstraints.pre, solverState.solver.formulaManager)
          ?.let { fn.addAnnotation(it) }
        postAnnotation(declarationConstraints.post, solverState.solver.formulaManager)
          ?.let { fn.addAnnotation(it) }
        if (model.isALaw()) {
          getIrReturnedExpressionWithoutPostcondition(fn)?.let { fnDescriptor ->
            lawSubjectAnnotation(fnDescriptor.withAliasUnwrapped)?.let { fn.addAnnotation(it) }
          }
        }
      }
    }
  }
}

private fun getIrReturnedExpressionWithoutPostcondition(
  function: IrFunction
): FunctionDescriptor? {
  val lastElement = function.body?.statements?.lastOrNull()
  val lastElementWithoutReturn = when (lastElement) {
    is IrReturn -> lastElement.value
    else -> lastElement
  }
  // remove outer layer of postcondition
  val veryLast: IrStatement? = when (lastElementWithoutReturn) {
    is IrMemberAccessExpression<*> -> when (val symbol = lastElementWithoutReturn.symbol) {
      is IrFunctionSymbol ->
        lastElementWithoutReturn.extensionReceiver
          .takeIf { symbol.owner.toIrBasedDescriptor().fqNameSafe == FqName("arrow.analysis.post") }
      else -> null
    }
    else -> null
  } ?: lastElementWithoutReturn
  // and finally obtain the model
  return when (veryLast) {
    is IrMemberAccessExpression<*> -> when (val symbol = veryLast.symbol) {
      is IrFunctionSymbol -> symbol.owner.toIrBasedDescriptor()
        .model<org.jetbrains.kotlin.descriptors.FunctionDescriptor, FunctionDescriptor>()
      else -> null
    }
    else -> null
  }
}

private fun IrMutableAnnotationContainer.addAnnotation(annotation: IrConstructorCall) {
  this.annotations = this.annotations + listOf(annotation)
}

private fun IrUtils.preAnnotation(formulae: List<NamedConstraint>, manager: FormulaManager): IrConstructorCall? =
  annotationFromClassId(ClassId.fromString("arrow/analysis/Pre"),
    formulae.map { it.msg },
    formulae.map { it.formula.toString() },
    formulae.flatMap { manager.fieldNames(it.formula).map { fld -> fld.first }.toSet() })

private fun IrUtils.postAnnotation(formulae: List<NamedConstraint>, manager: FormulaManager): IrConstructorCall? =
  annotationFromClassId(ClassId.fromString("arrow/analysis/Post"),
    formulae.map { it.msg },
    formulae.map { it.formula.toString() },
    formulae.flatMap { manager.fieldNames(it.formula).map { fld -> fld.first }.toSet() })

private fun IrUtils.lawSubjectAnnotation(descriptor: FunctionDescriptor): IrConstructorCall? =
  lawSubjectAnnotationFromClassId(ClassId.fromString("arrow/analysis/Subject"), descriptor)

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

private fun IrUtils.lawSubjectAnnotationFromClassId(classId: ClassId, descriptor: FunctionDescriptor): IrConstructorCall? =
  moduleFragment.descriptor.findClassAcrossModuleDependencies(classId)?.let {
    lawSubjectAnnotation(descriptor, it)
  }

private fun IrUtils.lawSubjectAnnotation(fnDescriptor: FunctionDescriptor, descriptor: ClassDescriptor): IrConstructorCall? =
  descriptor.irConstructorCall()?.also {
    it.putValueArgument(0, constantValue(fnDescriptor.fqNameSafe.name))
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
