plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
  explicitApiWarning()
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  api("io.arrow-kt:arrow-core:1.0.1-SNAPSHOT")
  api(projects.arrowAnalysisTypes)
  api("org.sosy-lab:java-smt:3.10.1")
}
