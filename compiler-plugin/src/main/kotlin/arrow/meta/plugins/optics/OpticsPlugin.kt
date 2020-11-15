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
import arrow.meta.plugins.optics.internals.optionalErrorMessage
import arrow.meta.plugins.optics.internals.process
import arrow.meta.plugins.optics.internals.targets
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import org.jetbrains.kotlin.psi.KtClass

val Meta.optics: CliPlugin
  get() =
    "optics" {
      meta(
        classDeclaration(this, ::isProductType) { c: KtClass ->
          if (c.companionObjects.isEmpty())
            knownError("@optics annotated class $c needs to declare companion object.", c)
          val files = ctx.process(listOf(adt(c)))
          Transform.newSources(*files.toTypedArray())
        }
      )
    }

private fun CompilerContext.adt(c: KtClass): ADT =
  ADT(c.containingKtFile.packageFqName, c, c.targets().map { target ->
    when (target) {
      OpticsTarget.LENS -> ctx.evalAnnotatedDataClass(c, c.lensErrorMessage).let(::LensTarget)
      OpticsTarget.OPTIONAL -> evalAnnotatedDataClass(c, c.optionalErrorMessage).let(::OptionalTarget)
      OpticsTarget.ISO -> evalAnnotatedIsoElement(c).let(::IsoTarget)
      OpticsTarget.PRISM -> evalAnnotatedPrismElement(c).let(::PrismTarget)
      OpticsTarget.DSL -> evalAnnotatedDslElement(c)
    }
  })

val opticsAnnotation: Regex = Regex("@(arrow\\.)?Optics")

fun isProductType(ktClass: KtClass): Boolean =
  ktClass.isData() &&
    ktClass.isAnnotatedWith(opticsAnnotation)

