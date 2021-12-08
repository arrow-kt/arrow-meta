@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

version = property("projects.analysis_version").toString()

kotlin {
  explicitApi = null
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  implementation(projects.arrowMeta)
  implementation(projects.arrowAnalysisTypes)
  implementation(projects.arrowAnalysisCommon)
  api(files(org.gradle.internal.jvm.Jvm.current().toolsJar))

  testImplementation(libs.kotlin.stdlibJDK8)
  testImplementation(libs.junit)
  testImplementation(libs.javaCompileTesting)
  testImplementation(projects.arrowMetaTest)
  testRuntimeOnly(projects.arrowMeta)
  testRuntimeOnly(projects.arrowAnalysisTypes)
  testRuntimeOnly(projects.arrowMetaPrelude)
  testRuntimeOnly(projects.arrowAnalysisJavaPlugin)
  testRuntimeOnly(projects.arrowAnalysisTypes)
  testRuntimeOnly(libs.arrowCore)
}
