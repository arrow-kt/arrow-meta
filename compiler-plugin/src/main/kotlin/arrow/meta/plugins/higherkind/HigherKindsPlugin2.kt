package arrow.meta.plugins.higherkind

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.quotes.Transform
import arrow.meta.quotes.classDeclaration
import arrow.meta.quotes.scope
import org.jetbrains.kotlin.psi.KtTypeAlias


val Meta.higherKindedTypes2: CliPlugin
  get() =
    "higherKindedTypes2" {
      meta(
        classDeclaration(this, ::isHigherKindedType) { c ->
          Transform.replace(c, listOfNotNull(
            /** Kind Marker **/
            "class For$name private constructor()".`class`.syntheticScope,
            /** Single arg type alias **/
            "typealias ${name}Of<${`(typeParameters)`.invariant()}> = arrowx.Kind${c.kindAritySuffix}<For$name, ${`(typeParameters)`.invariant()}>".declaration<KtTypeAlias>().syntheticScope,
            """|@arrow.Proof(arrow.TypeProof.Subtyping)
               |@Suppress("UNCHECKED_CAST")
               |inline fun <${`(typeParameters)`.invariant(true)}> ${name}Of<${`(typeParameters)`.invariant()}>.fix(): $name<${`(typeParameters)`.invariant()}> =
               |  (this as arrowx.Kinded).value as $name<${`(typeParameters)`.invariant()}>
               |""".function.syntheticScope,
            """|@arrow.Proof(arrow.TypeProof.Subtyping)
               |@Suppress("UNCHECKED_CAST")
               |inline fun <${`(typeParameters)`.invariant(true)}> $name<${`(typeParameters)`.invariant()}>.unfix(): ${name}Of<${`(typeParameters)`.invariant()}> =
               |  arrowx.Kinded(this)
               |""".function.syntheticScope,
            /** generate partial aliases if this kind has > 1 type parameters **/
            /** generate partial aliases if this kind has > 1 type parameters **/
            if (c.arity > 1)
              "typealias ${name}PartialOf<${c.partialTypeParameters}> = arrowx.Kind${c.partialKindAritySuffix}<For$name, ${c.partialTypeParameters}>".declaration<KtTypeAlias>().syntheticScope
            else null,
            c.scope()
          )
          )
        }
      )
    }