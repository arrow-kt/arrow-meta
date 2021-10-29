package arrow.meta.plugin.gradle

import org.gradle.api.provider.Property

public interface ArrowMetaExtension {
  public val generatedSrcOutputDir: Property<String>
}
