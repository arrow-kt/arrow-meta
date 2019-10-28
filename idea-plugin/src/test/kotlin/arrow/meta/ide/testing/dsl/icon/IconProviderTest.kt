package arrow.meta.ide.testing.dsl.icon

import arrow.meta.ide.testing.env.IdeTestTypeSyntax
import com.intellij.openapi.util.Iconable
import org.junit.Assert

object IconProviderTest {

  fun IdeTestTypeSyntax.test(code: String) =
    lightTest {
      // former IdeHigherKindesTestCode.
      code.sequence { psi ->
        // most implementations of PsiElement, including the default implementation,
        // retrieve the return value of getIcon() from the registered IconProviders.
        val icon = psi.getIcon(Iconable.ICON_FLAG_VISIBILITY)
        Assert.assertNotNull(icon)
        // fixme: test for a specific icon as soon as the compiler plugins
        //  provide custom icons for PSI elements.
        // this kind of icon is unrelated to gutter icons

        // fixme: this is not yet working, because
        //   - the kotlin class was created on the fly and the Kotlin compiler plugin has
        //   not yet been run on this source file
        //   Possible fixes:
        //   - integrate the Kotlin compiler and the Arrow compiler plugin somehow at runtime
        //   - don't create a file on-the-fly, but store it on disk. Generate the arrow bytecode and add
        //     the generated bytecode data to the vcs.
        //     Then make use of it in the tests, i.e. load the source from disk

        // TODO: uncomment when we have the heavy tests working
        //val psiType = (psi as? KtProperty)?.type()
        //Assert.assertTrue("psi element must resolve to a valid type", psiType != null && psiType !is UnresolvedType)
      }
    }
}
