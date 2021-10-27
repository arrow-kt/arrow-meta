plugins {
  alias(libs.plugins.arrowGradleConfig.jvm)
  alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
  explicitApiWarning()
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(projects.arrowMeta)

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0")
    testImplementation(projects.arrowMetaTest)
    testRuntimeOnly(projects.arrowMeta)
    testRuntimeOnly(projects.arrowMetaPrelude)
    testRuntimeOnly(projects.arrowProofsPlugin)
    testRuntimeOnly("io.arrow-kt:arrow-core:1.0.0")
}
