plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
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
}
