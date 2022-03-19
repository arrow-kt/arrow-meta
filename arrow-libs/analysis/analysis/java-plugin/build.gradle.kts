@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.publish)
  alias(libs.plugins.arrowGradleConfig.versioning)
  alias(libs.plugins.kotlin.binaryCompatibilityValidator)
}

kotlin {
  explicitApi = null
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  implementation(libs.arrowMeta)
  implementation(projects.arrowAnalysisTypes)
  implementation(projects.arrowAnalysisCommon)
  api(files(org.gradle.internal.jvm.Jvm.current().toolsJar))

  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junit)
  testImplementation(libs.javaCompileTesting)
  testImplementation(libs.arrowMetaTest)
  testRuntimeOnly(libs.arrowMeta)
  testRuntimeOnly(projects.arrowAnalysisTypes)
  testRuntimeOnly(projects.arrowAnalysisJavaPlugin)
  testRuntimeOnly(projects.arrowAnalysisTypes)
  testRuntimeOnly(libs.arrowCore)
}
