plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

version = property("projects.proofs_version").toString()

kotlin {
  explicitApi = null
}

dependencies {
    compileOnly(libs.kotlin.stdlibJDK8)
    implementation(projects.arrowMeta)

    testImplementation(libs.kotlin.stdlibJDK8)
    testImplementation(libs.junit)
    testImplementation(projects.arrowMetaTest)
    testRuntimeOnly(projects.arrowMeta)
    testRuntimeOnly(projects.arrowMetaPrelude)
    testRuntimeOnly(projects.arrowProofsPlugin)
    testRuntimeOnly(libs.arrowCore)
}
