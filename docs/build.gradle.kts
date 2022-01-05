import arrow.ank.AnkExtension

buildscript {
  repositories {
    maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev/")
    mavenCentral()
  }
  dependencies {
    classpath(libs.arrowAnkGradle)
  }
}

plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
}

apply(plugin = "ank-gradle-plugin")

dependencies {
  runtimeOnly(libs.kotlin.stdlibJDK8)
  runtimeOnly(projects.arrowMeta)
  runtimeOnly(projects.arrowAnalysisTypes)
}

configure<AnkExtension> {
  source = file("$projectDir/docs")
  target = file("$projectDir/build/site")
  classpath = sourceSets["main"].runtimeClasspath
}

tasks {
  named<Delete>("clean") {
    delete("$rootDir/docs/docs/apidocs")
  }

  compileKotlin {
    kotlinOptions.freeCompilerArgs += listOf("-Xskip-runtime-version-check")
  }
}
