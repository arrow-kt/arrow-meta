plugins {
  id(libs.plugins.kotlin.multiplatform.get().pluginId)
  alias(libs.plugins.arrowGradleConfig.kotlin)
  alias(libs.plugins.arrowGradleConfig.publish)
}

version = property("projects.analysis_version").toString()

kotlin {
  explicitApi = null

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlin.stdlibCommon)
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
    dependsOn(":arrow-analysis-kotlin-plugin:jar")
    freeCompilerArgs = listOf(
      "-Xplugin=$rootDir/plugins/analysis/kotlin-plugin/build/libs/arrow-analysis-kotlin-plugin-$version.jar",
      "-P", "plugin:arrow.meta.plugin.compiler.analysis:generatedSrcOutputDir=$buildDir/generated/meta"
    )
  }
}

tasks.compileKotlinJs {
  kotlinOptions.suppressWarnings = true
}

tasks.compileKotlinMetadata {
  kotlinOptions.suppressWarnings = true
}
