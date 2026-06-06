plugins {
    alias(libs.plugins.shadow)
}

dependencies {
    compileOnly(libs.adventure)

    implementation(project(":totaleconomy-api"))
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.kotlin.result)
    implementation(libs.hikari)

    testImplementation(kotlin("test"))
    testImplementation(libs.assertj)
    testImplementation(libs.h2)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.mockk)

    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks {
    test {
        useJUnitPlatform()
    }
}
