plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

version = property("projects.meta_version").toString()

kotlin {
  explicitApi = null
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  implementation(libs.classgraph)

  implementation(libs.kotlin.compilerEmbeddable)
  implementation(libs.kotlin.annotationProcessingEmbeddable)
  implementation(libs.kotlinCompileTesting) {
    exclude(group = libs.classgraph.get().module.group, module = libs.classgraph.get().module.name)
    exclude(
      group = libs.kotlin.compilerEmbeddable.get().module.group,
      module = libs.kotlin.compilerEmbeddable.get().module.name
    )
    exclude(
      group = libs.kotlin.annotationProcessingEmbeddable.get().module.group,
      module = libs.kotlin.annotationProcessingEmbeddable.get().module.name
    )
    exclude(
      group = libs.kotlin.stdlibJDK8.get().module.group,
      module = libs.kotlin.stdlibJDK8.get().module.name
    )
  }
  implementation(libs.assertj)
  implementation(projects.arrowMeta)

  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junit)
  testRuntimeOnly(projects.arrowMetaPrelude)
}
