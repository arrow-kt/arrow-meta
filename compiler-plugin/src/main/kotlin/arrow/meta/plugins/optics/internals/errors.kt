package arrow.meta.plugins.optics.internals

import org.jetbrains.kotlin.psi.KtAnnotated

val KtAnnotated.otherClassTypeErrorMessage
  get() = """
      |$this cannot be annotated with @Optics
      | ^
      |
      |Only data and sealed classes can be annotated with @Optics annotation""".trimMargin()

val KtAnnotated.lensErrorMessage
  get() = """
      |Cannot generate arrow.meta.plugins.optics.internals.Lens for $this
      |                                       ^
      |arrow.optics.OpticsTarget.LENS is an invalid @Optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

val KtAnnotated.optionalErrorMessage
  get() = """
      |Cannot generate arrow.meta.plugins.optics.internals.Optional for $this
      |                                           ^
      |arrow.optics.OpticsTarget.OPTIONAL is an invalid @Optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

val KtAnnotated.prismErrorMessage
  get() = """
      |Cannot generate arrow.meta.plugins.optics.internals.Prism for $this
      |                                        ^
      |arrow.optics.OpticsTarget.PRISM is an invalid @Optics argument for $this.
      |It is only valid for sealed classes.
      """.trimMargin()

val KtAnnotated.isoErrorMessage
  get() = """
      |Cannot generate arrow.meta.plugins.optics.internals.Iso for $this
      |                                      ^
      |arrow.optics.OpticsTarget.ISO is an invalid @Optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

val KtAnnotated.isoTooBigErrorMessage
  get() = """
      |Cannot generate arrow.meta.plugins.optics.internals.Iso for $this
      |                                      ^
      |Iso generation is supported for data classes with up to 22 constructor parameters.
      """.trimMargin()

val KtAnnotated.dslErrorMessage
  get() = """
      |Cannot generate DSL (arrow.optics.BoundSetter) for $this
      |                                           ^
      |arrow.optics.OpticsTarget.DSL is an invalid @Optics argument for $this.
      |It is only valid for data classes and sealed classes.
      """.trimMargin()
