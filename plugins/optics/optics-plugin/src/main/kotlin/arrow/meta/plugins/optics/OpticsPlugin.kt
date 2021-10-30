package arrow.meta.plugins.optics

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.phases.analysis.isAnnotatedWith
import arrow.meta.plugins.optics.internals.ADT
import arrow.meta.plugins.optics.internals.IsoTarget
import arrow.meta.plugins.optics.internals.LensTarget
import arrow.meta.plugins.optics.internals.OpticsTarget
import arrow.meta.plugins.optics.internals.OptionalTarget
import arrow.meta.plugins.optics.internals.PrismTarget
import arrow.meta.plugins.optics.internals.evalAnnotatedDataClass
import arrow.meta.plugins.optics.internals.evalAnnotatedDslElement
import arrow.meta.plugins.optics.internals.evalAnnotatedIsoElement
import arrow.meta.plugins.optics.internals.evalAnnotatedPrismElement
import arrow.meta.plugins.optics.internals.knownError
import arrow.meta.plugins.optics.internals.lensErrorMessage
import arrow.meta.plugins.optics.internals.noCompanion
import arrow.meta.plugins.optics.internals.optionalErrorMessage
import arrow.meta.plugins.optics.internals.process
import arrow.meta.plugins.optics.internals.targets
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import org.jetbrains.kotlin.psi.KtClass

val Meta.optics: CliPlugin
  get() = "optics" {
    meta(
      classDeclaration(this, { isOpticsTarget(element) }) { c ->
        if (c.element.companionObjects.isEmpty())
          knownError(c.element.nameAsSafeName.asString().noCompanion, c.element)
        val files = ctx.process(listOf(adt(c.element)))
        Transform.newSources(*files.toTypedArray())
      }
    )
  }

private fun CompilerContext.adt(c: KtClass): ADT =
  ADT(
    c.containingKtFile.packageFqName,
    c,
    c.targets().map { target ->
      when (target) {
        OpticsTarget.LENS ->
          ctx.evalAnnotatedDataClass(c, c.name!!.lensErrorMessage).let(::LensTarget)
        OpticsTarget.OPTIONAL ->
          evalAnnotatedDataClass(c, c.name!!.optionalErrorMessage).let(::OptionalTarget)
        OpticsTarget.ISO -> evalAnnotatedIsoElement(c).let(::IsoTarget)
        OpticsTarget.PRISM -> evalAnnotatedPrismElement(c).let(::PrismTarget)
        OpticsTarget.DSL -> evalAnnotatedDslElement(c)
      }
    }
  )

val opticsAnnotation: Regex = Regex("@(arrow\\.)?Optics")

fun isOpticsTarget(ktClass: KtClass): Boolean =
  (ktClass.isData() || ktClass.isSealed()) && ktClass.isAnnotatedWith(opticsAnnotation)
