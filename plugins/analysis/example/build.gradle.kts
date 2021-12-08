plugins {
  alias(libs.plugins.arrowGradleConfig.multiplatform)
}

version = property("projects.analysis_version").toString()

kotlin {
  explicitApi = null

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
        api(projects.arrowAnalysisLaws)
        api(projects.arrowAnalysisTypes)
      }
    }

    named("jvmMain") {
      dependencies {
        implementation(libs.kotlin.stdlibJDK8)
      }
    }

    named("jsMain") {
      dependencies {
        implementation(libs.kotlin.stdlibJS)
      }
    }
  }
}

dependencies {
  kotlinCompilerClasspath(projects.arrowAnalysisKotlinPlugin)
}

tasks.compileKotlinJvm {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xplugin=$rootDir/plugins/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-$version.jar",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:generatedSrcOutputDir=$buildDir/generated/meta"
    )
  }
}
