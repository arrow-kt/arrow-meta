plugins {
    alias(libs.plugins.arrowGradleConfig.jvm)
    alias(libs.plugins.arrowGradleConfig.publishJvm)
}

kotlin {
    explicitApiWarning()
}

dependencies {
    compileOnly(libs.kotlin.stdlibJDK8)
    implementation(projects.arrowMeta)

    testImplementation(libs.kotlin.stdlibJDK8)
    testImplementation(libs.junit)
    testImplementation(projects.arrowMetaTest)
    testRuntimeOnly(projects.arrowMeta)
    testRuntimeOnly(projects.arrowMetaPrelude)
    testRuntimeOnly(projects.arrowOpticsPlugin)
    testRuntimeOnly(libs.arrowCore)
    testRuntimeOnly(libs.arrowOptics)
}
