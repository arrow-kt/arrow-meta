package arrow.meta.ide.plugins.proofs.intentions

// deprecated until #
/*fun IdeMetaPlugin.makeImplicitCoercionIntention(compilerContext: CompilerContext): ExtensionPhase =
  addIntention(
    text = "Make coercion implicit",
    kClass = KtElement::class.java,
    isApplicableTo = { ktCall: KtElement, _ ->
      ktCall.implicitParticipatingTypes()?.let { (subtype, supertype) ->
        compilerContext.areTypesCoerced(subtype, supertype)
      } ?: false
    },
    applyTo = { ktCall: KtElement, _ ->
      when (ktCall) {
        is KtDotQualifiedExpression -> {
          ktCall.replace(ktCall.receiverExpression)
        }
      }
    }
  )

fun KtElement.implicitParticipatingTypes(): List<PairTypes> =
  when (this) {

    is KtDotQualifiedExpression ->
      (receiverExpression.resolveType() pairOrNull selectorExpression?.resolveType())?.let(::listOf)
        ?: emptyList()

    else -> emptyList()
  }
 */
