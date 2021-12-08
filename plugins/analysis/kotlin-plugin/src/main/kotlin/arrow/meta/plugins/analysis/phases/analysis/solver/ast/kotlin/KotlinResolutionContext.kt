package arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin

import arrow.meta.plugins.analysis.errors.MetaErrors
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.ResolutionContext
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.DeclarationDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.PropertyDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.descriptors.ValueParameterDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Declaration
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.Element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.FqName
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.elements.TypeReference
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Type
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.context.types.Types
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.element
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.ast.model
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.descriptors.KotlinModuleDescriptor
import arrow.meta.plugins.analysis.phases.analysis.solver.ast.kotlin.types.KotlinType
import arrow.meta.plugins.analysis.phases.analysis.solver.errors.ErrorIds
import arrow.meta.plugins.analysis.phases.analysis.solver.state.SolverState
import org.jetbrains.kotlin.cfg.getDeclarationDescriptorIncludingConstructors
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.callExpressionRecursiveVisitor
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.BindingTrace

class KotlinResolutionContext(
  private val state: SolverState?,
  private val impl: BindingTrace,
  private val moduleImpl: org.jetbrains.kotlin.descriptors.ModuleDescriptor
) : ResolutionContext, BindingTrace by impl {

  fun reportLiskovProblem(expression: Element, msg: String) {
    report(MetaErrors.LiskovProblem.on(expression.element(), msg))
  }

  fun reportUnsatInvariants(expression: Element, msg: String) {
    report(MetaErrors.UnsatInvariants.on(expression.element(), msg))
  }

  fun reportInconsistentInvariants(expression: Element, msg: String) {
    report(MetaErrors.InconsistentInvariants.on(expression.element(), msg))
  }

  fun reportInconsistentConditions(expression: Element, msg: String) {
    report(MetaErrors.InconsistentConditions.on(expression.element(), msg))
  }

  fun reportInconsistentCallPost(expression: Element, msg: String) {
    report(MetaErrors.InconsistentCallPost.on(expression.element(), msg))
  }

  private fun reportUnsatBodyPost(declaration: Element, msg: String) {
    report(MetaErrors.UnsatBodyPost.on(declaration.element(), msg))
  }

  private fun reportInconsistentBodyPre(declaration: Element, msg: String) {
    report(MetaErrors.InconsistentBodyPre.on(declaration.element(), msg))
  }

  fun reportUnsupported(expression: Element, msg: String) {
    report(MetaErrors.UnsupportedElement.on(expression.element(), msg))
  }

  fun reportAnalysisException(element: Element, msg: String) {
    report(MetaErrors.AnalysisException.on(element.element(), msg))
  }

  override val types: Types =
    object : Types {
      override val nothingType: Type = KotlinType(moduleImpl.builtIns.nothingType)
    }

  /**
   * Recursively walks [this] element for calls to [arrow.analysis.pre] and [arrow.analysis.post]
   * that hold preconditions
   */
  override fun Element.constraintsDSLElements(): List<Element> {
    val results = hashSetOf<PsiElement>()
    val visitor = callExpressionRecursiveVisitor {
      if (it.calleeExpression?.text == "pre" ||
          it.calleeExpression?.text == "post" ||
          it.calleeExpression?.text == "require"
      ) {
        results.add(it)
      }
    }
    val psi = element<Element, KtElement>()
    psi.accept(visitor)
    psi.acceptChildren(visitor)
    return results.filterIsInstance<KtElement>().map { it.model() }
  }

  override fun type(typeReference: TypeReference?): Type? =
    typeReference
      ?.let { (it.psiOrParent as? KtExpression)?.let { expr -> bindingContext.getType(expr) } }
      ?.let { KotlinType(it) }

  override fun handleError(error: ErrorIds, element: Element, msg: String): Unit {
    when (error) {
      ErrorIds.Exception.IllegalState -> reportAnalysisException(element, msg)
      ErrorIds.Exception.OtherException -> reportAnalysisException(element, msg)
      ErrorIds.Inconsistency.InconsistentBodyPre -> reportInconsistentBodyPre(element, msg)
      ErrorIds.Inconsistency.InconsistentDefaultValues -> reportInconsistentBodyPre(element, msg)
      ErrorIds.Inconsistency.InconsistentConditions -> reportInconsistentConditions(element, msg)
      ErrorIds.Inconsistency.InconsistentCallPost -> reportInconsistentCallPost(element, msg)
      ErrorIds.Inconsistency.InconsistentInvariants -> reportInconsistentInvariants(element, msg)
      ErrorIds.Liskov.NotWeakerPrecondition -> reportLiskovProblem(element, msg)
      ErrorIds.Liskov.NotStrongerPostcondition -> reportLiskovProblem(element, msg)
      ErrorIds.Parsing.ErrorParsingPredicate -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Parsing.UnexpectedReference -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Parsing.UnexpectedFieldInitBlock -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.LawMustCallFunction -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.LawMustHaveParametersInOrder -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.SubjectWithoutName -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Laws.CouldNotResolveSubject -> reportErrorsParsingPredicate(element, msg)
      ErrorIds.Unsatisfiability.UnsatCallPre -> reportUnsatCallPre(element, msg)
      ErrorIds.Unsatisfiability.UnsatBodyPost -> reportUnsatBodyPost(element, msg)
      ErrorIds.Unsatisfiability.UnsatInvariants -> reportUnsatInvariants(element, msg)
      ErrorIds.Unsupported.UnsupportedImplicitPrimaryConstructor -> reportUnsupported(element, msg)
      ErrorIds.Unsupported.UnsupportedExpression -> reportUnsupported(element, msg)
    }
    state?.notifySarifReport(error, element, msg)
  }

  private fun reportErrorsParsingPredicate(element: Element, msg: String) {
    report(MetaErrors.ErrorParsingPredicate.on(element.element(), msg))
  }

  private fun reportUnsatCallPre(element: Element, msg: String) {
    report(MetaErrors.UnsatCallPre.on(element.element(), msg))
  }

  private val descriptorCache: MutableMap<String, List<DeclarationDescriptor>> = mutableMapOf()
  override fun descriptorFor(fqName: FqName): List<DeclarationDescriptor> {
    // add if not there yet
    if (!descriptorCache.containsKey(fqName.name)) {
      val info: List<DeclarationDescriptor> = descriptorWorker(fqName).map { it.model() }
      descriptorCache[fqName.name] = info
    }
    return descriptorCache.getValue(fqName.name)
  }

  private fun descriptorWorker(
    fqName: FqName
  ): List<org.jetbrains.kotlin.descriptors.DeclarationDescriptor> {
    val portions = fqName.name.split(".")

    fun org.jetbrains.kotlin.descriptors.ClassDescriptor.getAllDescriptors() =
      unsubstitutedMemberScope.getContributedDescriptors { true } +
        staticScope.getContributedDescriptors { true } +
        unsubstitutedInnerClassesScope.getContributedDescriptors { true }

    var current: List<org.jetbrains.kotlin.descriptors.DeclarationDescriptor> =
      listOfNotNull(moduleImpl.getPackage(org.jetbrains.kotlin.name.FqName(portions.first())))
    for (portion in portions.drop(1)) {
      current =
        when (portion) {
          "<init>" ->
            current.flatMap {
              when (it) {
                is org.jetbrains.kotlin.descriptors.ClassDescriptor -> it.constructors
                is org.jetbrains.kotlin.descriptors.TypeAliasDescriptor -> it.constructors
                else -> emptyList()
              }
            }
          else ->
            current.flatMap { decl ->
              when (decl) {
                  is org.jetbrains.kotlin.descriptors.PackageViewDescriptor ->
                    decl.memberScope.getContributedDescriptors { true }
                  is org.jetbrains.kotlin.descriptors.ClassDescriptor -> decl.getAllDescriptors()
                  is org.jetbrains.kotlin.descriptors.TypeAliasDescriptor ->
                    decl.classDescriptor?.getAllDescriptors()
                  else -> null
                }
                .orEmpty()
                .flatMap {
                  when (it) {
                    is org.jetbrains.kotlin.descriptors.PropertyDescriptor ->
                      listOfNotNull(it, it.getter, it.setter)
                    else -> listOf(it)
                  }
                }
                .filter { it.name.asString() == portion }
            }
        }
    }

    return current
  }

  override fun descriptorFor(declaration: Declaration): DeclarationDescriptor? =
    (declaration.impl() as? KtDeclaration)
      ?.getDeclarationDescriptorIncludingConstructors(impl.bindingContext)
      ?.model()

  override fun backingPropertyForConstructorParameter(
    parameter: ValueParameterDescriptor
  ): PropertyDescriptor? =
    (parameter.impl() as? org.jetbrains.kotlin.descriptors.ValueParameterDescriptor)?.let {
      impl.bindingContext.get(BindingContext.VALUE_PARAMETER_AS_PROPERTY, it)?.model()
    }

  override val module: ModuleDescriptor = KotlinModuleDescriptor(moduleImpl)
}
