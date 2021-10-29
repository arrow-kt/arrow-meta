plugins {
  alias(libs.plugins.arrowGradleConfig.multiplatform)
  alias(libs.plugins.arrowGradleConfig.publishMultiplatform)
}

kotlin {
  explicitApiWarning()

  sourceSets {
    commonMain {
      dependencies {
        compileOnly(libs.kotlin.stdlibCommon)
        api(projects.arrowAnalysisTypes)
      }
    }

    named("jvmMain") {
      dependencies {
        compileOnly(libs.kotlin.stdlibJDK8)
      }
    }

    named("jsMain") {
      dependencies {
        compileOnly(libs.kotlin.stdlibJS)
      }
    }
  }
}

dependencies {
  compileOnly(libs.kotlin.stdlibJDK8)
  kotlinCompilerClasspath(projects.arrowAnalysisKotlinPlugin)
}

tasks.compileKotlinJvm {
  kotlinOptions {
    dependsOn(":arrow-analysis-kotlin-plugin:jar")
    freeCompilerArgs = listOf(
      "-Xplugin=$rootDir/plugins/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-1.5.31-SNAPSHOT.jar",
      "-P", "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=$buildDir/generated/meta"
    )
  }
}

tasks.compileKotlinJs {
  kotlinOptions.suppressWarnings = true
}

tasks.compileKotlinMetadata {
  kotlinOptions.suppressWarnings = true
}
