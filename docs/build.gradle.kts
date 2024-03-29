@file:Suppress("DSL_SCOPE_VIOLATION")

buildscript {
  repositories {
    mavenCentral()
  }
}

plugins {
  id(libs.plugins.kotlin.jvm.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
}

kotlin {
  explicitApi = null

  val jvmTargetVersion = properties["jvmTargetVersion"].toString()
  val javaVersion = if (jvmTargetVersion == "1.8") "8" else jvmTargetVersion
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(javaVersion))
  }
}

dependencies {
  runtimeOnly(libs.kotlin.stdlibJDK8)
  runtimeOnly(projects.arrowMeta)
}

tasks {
  named<Delete>("clean") {
    delete("$rootDir/docs/docs/apidocs")
  }

  compileKotlin {
    kotlinOptions.freeCompilerArgs += listOf("-Xskip-runtime-version-check")
  }
}
