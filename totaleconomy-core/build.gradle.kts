plugins {
    java
    alias(libs.plugins.shadow)
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    implementation(project(":totaleconomy-api"))
    implementation(libs.adventure)
    implementation(libs.hikari)

    testImplementation(libs.assertj)
    testImplementation(libs.h2)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.adventure)

    mockitoAgent(libs.mockito.core) { isTransitive = false }

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks {
    test {
        useJUnitPlatform()

        doFirst {
            jvmArgs("-javaagent:${mockitoAgent.singleFile.absolutePath}")
        }
    }
}
