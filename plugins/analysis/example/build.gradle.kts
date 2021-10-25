plugins {
  alias(libs.plugins.arrowGradleConfig.multiplatform)
}

kotlin {
  explicitApiWarning()

  sourceSets {
    commonMain {
      dependencies {
        compileOnly(libs.kotlin.stdlibCommon)
        api(projects.analysisLaws)
        api(projects.analysisTypes)
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
  kotlinCompilerClasspath(projects.analysisKotlinPlugin)
}

tasks.compileKotlinJvm {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xplugin=$rootDir/plugins/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-1.5.31-SNAPSHOT.jar",
      "-P", "plugin:arrow.meta.plugin.compiler:generatedSrcOutputDir=$buildDir"
    )
  }
}
