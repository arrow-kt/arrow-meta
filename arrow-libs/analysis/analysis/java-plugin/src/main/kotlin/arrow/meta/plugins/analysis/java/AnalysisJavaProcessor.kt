@file:Suppress("JAVA_MODULE_DOES_NOT_EXPORT_PACKAGE")

package arrow.meta.plugins.analysis.java

import com.sun.tools.javac.processing.JavacProcessingEnvironment
import com.sun.tools.javac.util.Context
import javax.annotation.processing.Completion
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

// The ultimate HACK: all elements of this class are equal across ClassLoaders!!
// This is needed for the next step to actually find the list of elements
public object AnalysisJavaProcessorKey : Context.Key<MutableList<Element>>() {
  override fun equals(other: Any?): Boolean {
    return other?.javaClass?.name == "arrow.meta.plugins.analysis.java.AnalysisJavaProcessorKey"
  }
  override fun hashCode(): Int = 12345
}

public class AnalysisJavaProcessor : Processor {

  public val todo: MutableList<Element> = mutableListOf()

  override fun getSupportedOptions(): MutableSet<String> = mutableSetOf()
  override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf("*")
  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun init(processingEnv: ProcessingEnvironment) {
    (processingEnv as? JavacProcessingEnvironment)?.context?.put(AnalysisJavaProcessorKey, todo)
  }

  override fun process(
    annotations: MutableSet<out TypeElement>,
    roundEnv: RoundEnvironment
  ): Boolean {
    todo.addAll(roundEnv.rootElements)
    return false
  }

  override fun getCompletions(
    element: Element?,
    annotation: AnnotationMirror?,
    member: ExecutableElement?,
    userText: String?
  ): MutableIterable<Completion> = mutableListOf()
}
