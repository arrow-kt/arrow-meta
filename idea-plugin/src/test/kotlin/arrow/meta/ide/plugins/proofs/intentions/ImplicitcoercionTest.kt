package arrow.meta.ide.plugins.proofs.intentions

/*
import arrow.meta.ide.testing.env.IdeTestSetUp
import com.intellij.codeHighlighting.Pass
import com.intellij.testFramework.fixtures.impl.CodeInsightTestFixtureImpl
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.core.moveCaret
import org.jetbrains.kotlin.psi.KtFile
import org.junit.Test

const val proof = """
import arrow.Proof
import arrow.Refined
import arrow.TypeProof

inline class TwitterHandle(val handle: String) {
    companion object : Refined<String, TwitterHandle> {
        override val constructor = ::TwitterHandle
        override val validate: String.() -> Map<String, Boolean> = {
            mapOf(
                "Should start with '@'" to startsWith("@"),
                "Should have length <= 16" to (length <= 16),
                "Should have length > 2" to (length > 2),
                "Should not contain the word 'twitter'" to !contains("twitter"),
                "Should not contain the word 'admin'" to !contains("admin")
            )
        }
    }
}

@Proof(TypeProof.Extension, coerce = true)
fun String.twitterHandle(): TwitterHandle? =
    TwitterHandle.from(this)
    
"""

class ImplicitcoercionTest : IdeTestSetUp() {


  @Test
  fun name() {
    val code = """
      package testArrow
      
      $proof
      
      val result: TwitterHandle? = "@admin"
      
    """.trimIndent()

    val file = myFixture.configureByText(KotlinFileType.INSTANCE, code)
    myFixture.openFileInEditor(file.virtualFile)

//    val inspection: AbstractKotlinInspection = TODO()
//    myFixture.enableInspections()
    myFixture.editor.moveCaret(myFixture.file.text.indexOf("@admin"))
//    val psi = myFixture.elementAtCaret

    val highlightInfos = CodeInsightTestFixtureImpl.instantiateAndRun(
      file, myFixture.editor, intArrayOf(
      Pass.LINE_MARKERS,
      Pass.EXTERNAL_TOOLS,
      Pass.POPUP_HINTS,
      Pass.UPDATE_ALL,
      Pass.UPDATE_FOLDING,
      Pass.WOLF
    ), (file as? KtFile)?.isScript() == true
    )
//      .filter { it.description != null /*&& caretOffset in it.startOffset..it.endOffset*/ }

    // TODO() currently only built-in inspections are instantiated, none from Meta
    println(highlightInfos)
  }
}
*/
