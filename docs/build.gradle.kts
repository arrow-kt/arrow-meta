buildscript {
  repositories {
    mavenCentral()
  }
}

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
}

dependencies {
  runtimeOnly(libs.kotlin.stdlibJDK8)
  runtimeOnly(projects.arrowMeta)
  runtimeOnly(projects.arrowAnalysisTypes)
}

tasks {
  named<Delete>("clean") {
    delete("$rootDir/docs/docs/apidocs")
  }

  compileKotlin {
    kotlinOptions.freeCompilerArgs += listOf("-Xskip-runtime-version-check")
  }
}
