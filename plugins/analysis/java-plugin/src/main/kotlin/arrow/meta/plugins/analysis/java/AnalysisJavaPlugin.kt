@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import arrow.meta.plugins.analysis.java.ast.JavaResolutionContext
import arrow.meta.plugins.analysis.java.ast.addAnn
import arrow.meta.plugins.analysis.java.ast.annArrays
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaDescriptor
import arrow.meta.plugins.analysis.java.ast.descriptors.JavaFunctionDescriptor
import arrow.meta.plugins.analysis.java.ast.elements.JavaElement
import arrow.meta.plugins.analysis.java.ast.elements.JavaMethod
import arrow.meta.plugins.analysis.java.ast.elements.OurTreeVisitor
import arrow.meta.plugins.analysis.java.ast.elements.visitRecursively
import arrow.meta.plugins.analysis.java.ast.model
import arrow.meta.plugins.analysis.java.ast.modelCautious
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.check.checkDeclarationConstraints
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromAnnotations
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.collectConstraintsFromDSL
import arrow.meta.plugins.analysis.phases.analysis.solver.collect.model.NamedConstraint
import arrow.meta.plugins.analysis.phases.analysis.solver.isALaw
import arrow.meta.plugins.analysis.phases.analysis.solver.search.getConstraintsFor
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import arrow.meta.plugins.analysis.smt.fieldNames
import arrow.meta.plugins.analysis.smt.utils.NameProvider
import com.sun.source.tree.CompilationUnitTree
import com.sun.source.tree.MethodTree
import com.sun.source.tree.Tree
import com.sun.source.util.JavacTask
import com.sun.source.util.Plugin
import com.sun.source.util.TaskEvent
import com.sun.tools.javac.api.BasicJavacTask
import com.sun.tools.javac.tree.JCTree
import javax.lang.model.element.Element

/*
javac does not guarantee any order between the different files in a project,
only that for each file you'll get a set of events PARSE, ENTER, ANALYZE, GENERATE.
This is not great, because we need to process the methods in the ANALYZE stage,
or otherwise the function calls cannot be resolved to their origins
(so we don't know whether a certain call is indeed to our `pre` function or to any
other function with the same name). Furthermore, we need to make the (SMT) checks
in the ANALYZE stage too, because in the GENERATE phase most of the nice structure
in the code has already been lowered (for example, lambdas have already been
turned into classes).

javac does guarantee, though, that annotation processors will be run before any
other PARSE events, so this seems a way out this mess. Alas, at that point the
function calls cannot be resolved yet (people often say that annotation processors
don't have access to the function body, which is not entirely correct, but in any
case the information we need is not there.)

Our solution is really dirty, but it works. We introduce an annotation processor
[AnalysisJavaProcessor] whose only task is to gather the elements that are read by
the compiler, but does nothing at that point. Then, once we are past the ANALYZE
stage (but before GENERATE), we process that list. javac uses a lot of mutability,
and in particular the bodies of the functions we gathered will now contain all the
information we need! Our final hack is to introduce an [executeOnce] function to
ensure that the processing only happens once in the entire run of the analyzer.
*/

public class AnalysisJavaPlugin : Plugin {
  override fun getName(): String = NAME

  override fun init(task: JavacTask?, vararg args: String?) {
    (task as? BasicJavacTask)?.let { task ->
      val solverState = SolverState(NameProvider())

      task.after(TaskEvent.Kind.ANALYZE) { _, unit: CompilationUnitTree ->
        AnalysisJavaProcessor.instance?.let { processor ->
          val ctx = AnalysisContext(task, unit)
          val resolutionContext = JavaResolutionContext(ctx)

          processor.executeOnce { todo ->
            // stage 1: collect constraints from DSL
            solverState.collectFromDsl(todo, ctx, resolutionContext)
            // stage 2: collect constraints from annotations
            solverState.collectFromAnnotations(todo, ctx, resolutionContext)
          }
          // stage 3: check the constraints
          solverState.checkConstraints(unit, ctx, resolutionContext)
        }
      }
    }
  }

  private fun SolverState.collectFromDsl(
    todo: List<Element>,
    ctx: AnalysisContext,
    resolutionContext: JavaResolutionContext
  ) {
    val obtainedPackages = mutableListOf<FqName>()
    todo.forEach { descriptor ->
      ctx
        .resolver
        .tree(descriptor)
        ?.visitRecursively(
          object : OurTreeVisitor<Unit>(Unit) {
            override fun visitMethod(node: MethodTree, p: Unit?) {
              val decl: JavaMethod = node.model(ctx)
              ctx.resolver.resolve(node)?.let { elt ->
                val descr: JavaFunctionDescriptor = elt.model(ctx)
                // stage 1a: collect the constraints
                decl.collectConstraintsFromDSL(this@collectFromDsl, resolutionContext, descr)
                // stage 1b: add the corresponding annotations
                val modifiers = node.modifiers as? JCTree.JCModifiers
                val constraints = getConstraintsFor(descr)
                if (modifiers != null && constraints != null) {
                  addConstraints(modifiers, "arrow.analysis.Pre", constraints.pre, ctx)
                  addConstraints(modifiers, "arrow.analysis.Post", constraints.post, ctx)
                  if (descr.isALaw()) {
                    // TODO: add the subject of the law
                  }
                  // add to the list of packages with annotations
                  descr.containingPackage?.let { obtainedPackages.add(it) }
                }
              }
            }
          }
        )
    }
    // write the final annotation
    /*ctx.context.get(JavaFileManager::class.java)?.let { fileManager ->
      fileManager.getJavaFileForOutput()
    }*/
  }

  private fun SolverState.addConstraints(
    modifiers: JCTree.JCModifiers,
    type: String,
    constraints: List<NamedConstraint>,
    ctx: AnalysisContext
  ) {
    if (constraints.isNotEmpty()) {
      modifiers.addAnn {
        ctx.annArrays(
          type,
          constraints.map { it.msg },
          constraints.map { it.formula.toString() },
          constraints.flatMap {
            solver.formulaManager.fieldNames(it.formula).map { fld -> fld.first }.toSet()
          }
        )
      }
    }
  }

  private fun SolverState.collectFromAnnotations(
    todo: List<Element>,
    ctx: AnalysisContext,
    resolutionContext: JavaResolutionContext
  ) {
    ctx.elements.allModuleElements.forEach { module ->
      collectConstraintsFromAnnotations(
        todo.map { it.model(ctx) },
        module.model(ctx),
        resolutionContext
      )
    }
  }

  private fun SolverState.checkConstraints(
    unit: CompilationUnitTree,
    ctx: AnalysisContext,
    resolutionContext: JavaResolutionContext
  ) {
    if (!hadParseErrors()) {
      unit.visitRecursively(
        object : OurTreeVisitor<Unit>(Unit) {
          override fun defaultAction(node: Tree, p: Unit?) {
            val decl: JavaElement? = node.modelCautious(ctx)
            if (decl is Declaration) {
              ctx.resolver.resolve(node)?.let { elt ->
                val descr: JavaDescriptor = elt.model(ctx)
                checkDeclarationConstraints(resolutionContext, decl, descr)
              }
            }
          }
        }
      )
    }
  }

  public companion object {
    public val NAME: String = "ArrowAnalysisJavaPlugin"
  }
}
