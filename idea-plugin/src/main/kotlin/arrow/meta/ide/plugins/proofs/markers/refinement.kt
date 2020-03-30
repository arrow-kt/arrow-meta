package arrow.meta.ide.plugins.proofs.markers

import arrow.meta.ide.IdeMetaPlugin
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.phases.Composite
import arrow.meta.phases.ExtensionPhase
import arrow.meta.phases.analysis.dfs
import arrow.meta.plugins.proofs.phases.quotes.isRefined
import arrow.meta.quotes.classorobject.ClassDeclaration
import arrow.meta.quotes.classorobject.ObjectDeclaration
import arrow.meta.quotes.element.ValueArgument
import arrow.meta.quotes.expression.BinaryExpression
import com.intellij.codeInsight.documentation.DocumentationManagerProtocol
import com.intellij.codeInsight.javadoc.JavaDocUtil
import com.intellij.icons.AllIcons
import com.intellij.psi.util.parentOfType
import com.intellij.psi.util.parentsOfType
import org.celtric.kotlin.html.Node
import org.celtric.kotlin.html.a
import org.celtric.kotlin.html.code
import org.celtric.kotlin.html.div
import org.celtric.kotlin.html.doctype
import org.celtric.kotlin.html.head
import org.celtric.kotlin.html.html
import org.celtric.kotlin.html.meta
import org.celtric.kotlin.html.p
import org.celtric.kotlin.html.render
import org.celtric.kotlin.html.table
import org.celtric.kotlin.html.tbody
import org.celtric.kotlin.html.td
import org.celtric.kotlin.html.th
import org.celtric.kotlin.html.thead
import org.celtric.kotlin.html.title
import org.celtric.kotlin.html.tr
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.findPropertyByName
import org.jetbrains.kotlin.utils.addToStdlib.safeAs
import org.celtric.kotlin.html.body as htmlBody

fun IdeMetaPlugin.refinementLineMarkers(): ExtensionPhase =
  Composite(
    refinedClassLineMarker(),
    predicateLineMarker()
  )

private fun IdeMetaPlugin.predicateLineMarker(): ExtensionPhase =
  addLineMarkerProvider(
    icon = AllIcons.Actions.Checked,
    transform = {
      it.safeAs<KtValueArgument>()?.takeIf(KtValueArgument::isRefinedPredicate)
    },
    message = {
      it.markerMessage()
    }
  )

fun KtValueArgument.markerMessage(): String =
  ValueArgument(this).run {
    parentOfType<KtClass>()?.let {
      ClassDeclaration(it).run {
        doctype("html") + html {
          head {
            title("$name") +
              meta(charset = "utf-8")
          } +
            htmlBody {
              div {
                p {
                  code("$name") + " is constrained by " + code("$argumentExpression")
                }
              }
            }
        }
      }.render()
    }.orEmpty()
  }

fun KtValueArgument.isRefinedPredicate(): Boolean =
  parentsOfType<KtProperty>().any { it.name == "validate" } &&
    parentsOfType<KtObjectDeclaration>().any { it.isRefined() } &&
    parent.parent is KtCallExpression &&
    parent.parent.text.startsWith("mapOf")

val validate: String.() -> Map<String, Boolean> = {
  mapOf(
    "Should start with '@'" to startsWith("@"),
    "Should have length <= 16" to (length <= 16),
    "Should have length > 2" to (length > 2),
    "Should not contain the word 'twitter'" to !contains("twitter"),
    "Should not contain the word 'admin'" to !contains("admin")
  )
}

private fun IdeMetaPlugin.refinedClassLineMarker(): ExtensionPhase =
  addLineMarkerProvider(
    icon = ArrowIcons.REFINEMENT,
    composite = KtClass::class.java,
    transform = {
      it.safeAs<KtClass>()?.takeIf { it.companionObjects.any { it.isRefined() } }
    },
    message = {
      it.markerMessage()
    }
  )

fun KtClass.markerMessage(): String =
  companionObjects.firstOrNull()?.let { ObjectDeclaration(it) }?.run {
    val predicates = this@markerMessage.predicatesFromPsi().map { BinaryExpression(it) }
    val document = doctype("html") + html {
      head {
        title("$name") +
          meta(charset = "utf-8")
      } +
        htmlBody {
          div {
            p {
              "$name is a Refined Type constrained by:"
            } + table {
              thead {
                tr {
                  th("Predicate") + th("Compile Time Expression")
                }
              } +
                tbody {
                  predicates.fold(emptyList<Node>()) { acc, predicate ->
                    acc + tr {
                      td(
                        a(
                          href = DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL + JavaDocUtil.getReferenceText(project, predicate.left?.value).orEmpty(),
                          target = "_blank"
                        ) { "${predicate.left}" }
                      ) + td(
                        a(
                          href = DocumentationManagerProtocol.PSI_ELEMENT_PROTOCOL + JavaDocUtil.getReferenceText(project, predicate.right?.value).orEmpty(),
                          target = "_blank"
                        ) { "${predicate.right}" }
                      )
                    }
                  }
                }
            }
          }
        }
    }
//    ul {
//      predicates.mapNotNull {
//        it.left?.text?.let(::li)
//      }
//    }.render()
    document.render()
  } ?: ""

private fun KtClass.predicatesFromPsi(): List<KtBinaryExpression> =
  companionObjects.firstOrNull()?.findPropertyByName("validate")
    ?.dfs { it is KtBinaryExpression }
    ?.filterIsInstance<KtBinaryExpression>()
    ?.filter { it.operationReference.text == "to" }
    .orEmpty()
