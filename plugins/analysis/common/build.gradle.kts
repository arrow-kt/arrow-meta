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
  api(libs.arrowCore)
  api(projects.arrowAnalysisTypes)
  api(libs.javaSmt)
  api(libs.apacheCommonsText)
  api(libs.sarif4k)
}
