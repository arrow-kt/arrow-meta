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
