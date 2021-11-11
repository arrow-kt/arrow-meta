@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("*")
public class AnalysisJavaProcessor : AbstractProcessor() {

  public companion object {
    public var instance: AnalysisJavaProcessor? = null
  }

  private var executed = false
  public val todo: MutableList<Element> = mutableListOf()

  override fun init(processingEnv: ProcessingEnvironment) {
    super.init(processingEnv)
    instance = this
  }

  override fun process(
    annotations: MutableSet<out TypeElement>,
    roundEnv: RoundEnvironment
  ): Boolean {
    todo.addAll(roundEnv.rootElements)
    return false
  }

  public fun executeOnce(f: (List<Element>) -> Unit) {
    if (executed) return
    f(todo)
    executed = true
  }
}
