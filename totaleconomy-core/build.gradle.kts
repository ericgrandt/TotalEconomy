plugins {
    java
    alias(libs.plugins.shadow)
}

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

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
