package arrow.meta.ide.testing.dsl.lineMarker

/*
import arrow.meta.ide.resources.ArrowIcons
import arrow.meta.ide.testing.env.types.LightTestSyntax
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviders
import com.intellij.psi.PsiElement
import com.intellij.psi.SyntaxTraverser
import com.intellij.psi.util.PsiTreeUtil
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.junit.Test

class OldLMTest  {
  @Language("kotlin")
  private val withMarkers = """
    package test
    import arrow.higherKind
    
    @higherkind
    class <caret>Id1<out A>(val value: A)
    
    @arrow.higherkind
    class <caret>Id2<out A>(val value: A)
     
    val x: IdOf<Int> = Id(1)
    """.trimIndent()

  @Language("kotlin")
  private val withoutMarkers = """
    package test
    import arrow.higherKind
  
    sealed class Sealed
    @higherKind
    data class <caret>IdSealed<out A>(val number: A) : Sealed()
    
    // missing @higherKind
    class <caret>IdNoHigherKind<out A>(val value: A)
    
    // annotation class
    @higherKind
    annotation class <caret>IdAnnotation<out A>(val value: A)
    
    // missing type parameter
    @higherKind
    annotation class <caret>IdNoTypeParameter(val value: Int)
    
    // missing marker, no type parameter
    class <caret>Outer {
      // nested class
      @higherkind
      class <caret>Nested<out A>(val value: A)
    }
    
    fun foo() {
      // not at top-level
      @higherkind
      class <caret>NotTopLevel<out A>(val value: A) 
    }
  
    // not a class
    val <caret>notAClass: IdOf<Int> = Id(1)
    """.trimIndent()

  @Test
  fun testAvailable() {
    // if possible, line marker providers should provide icons for leaf elements
    // as advised by com.intellij.codeInsight.daemon.LineMarkerProvider.getLineMarkerInfo().
    // Therefore, we retrieve the leaf element for the current element and call the icon providers on this leaf
    withMarkers.withEachCaret { psi ->
      val leaf = if (psi.firstChild == null) psi else PsiTreeUtil.getDeepestFirst(psi)

      assertEquals("expected one fast marker to be provided by arrow, element: ${leaf.text}", 0, leaf.fastArrowMarkers().size)

      assertEmpty("no slow markers expected, element: ${leaf.text}", leaf.slowArrowMarkers())

      if (leaf != psi) {
        SyntaxTraverser.psiTraverser().children(psi).filter { it.firstChild != null }.forEach { e ->
          assertEmpty("no faster markers expected, element: ${e.text}", psi.fastArrowMarkers())

          assertEmpty("no slow markers expected, element: ${e.text}", psi.slowArrowMarkers())
        }
      }
    }
  }

  @Test
  fun testUnavailable() {
    withoutMarkers.withEachCaret { psi ->
      assertEmpty("no fast markers exepcted for ${psi.text}", psi.fastArrowMarkers())

      assertEmpty("no slow markers exepcted for ${psi.text}", psi.slowArrowMarkers())
    }
  }

  private fun PsiElement.fastArrowMarkers(): List<LineMarkerInfo<PsiElement>> {
    return LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { it.getLineMarkerInfo(this) }
      .filter { it.icon == ArrowIcons.HKT }
  }

  private fun PsiElement.slowArrowMarkers(): List<LineMarkerInfo<PsiElement>> {
    val result = mutableListOf<LineMarkerInfo<PsiElement>>()
    LineMarkerProviders.INSTANCE.allForLanguage(KotlinLanguage.INSTANCE)
      .mapNotNull { it.collectSlowLineMarkers(listOf(this), result) }
    return result.filter { it.icon == ArrowIcons.HKT }
  }
}*/
