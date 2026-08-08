plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":totaleconomy-api"))
    implementation(project(":common"))

    implementation(libs.hikari)

    testImplementation(libs.h2)
    testImplementation(libs.hikari)
    testImplementation(libs.junit.jupiter.api)

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}