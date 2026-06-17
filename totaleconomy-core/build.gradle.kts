plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":totaleconomy-api"))
    implementation(libs.adventure)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.kotlin.result)
    implementation(libs.hikari)

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj)
    testImplementation(libs.h2)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.adventure)

    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
