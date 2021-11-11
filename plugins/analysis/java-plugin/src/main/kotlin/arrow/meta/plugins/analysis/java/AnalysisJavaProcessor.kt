@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import javax.annotation.processing.Completion
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

public class AnalysisJavaProcessor : Processor {

  public companion object {
    public var instance: AnalysisJavaProcessor? = null
  }

  private var executed = false
  public val todo: MutableList<Element> = mutableListOf()

  override fun getSupportedOptions(): MutableSet<String> = mutableSetOf()
  override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf("*")
  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun init(processingEnv: ProcessingEnvironment) {
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

  override fun getCompletions(
    element: Element?,
    annotation: AnnotationMirror?,
    member: ExecutableElement?,
    userText: String?
  ): MutableIterable<Completion> = mutableListOf()
}
