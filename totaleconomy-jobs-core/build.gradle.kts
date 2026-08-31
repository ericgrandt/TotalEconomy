plugins {
    id("java")
}

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    implementation(project(":totaleconomy-api"))
    implementation(project(":common"))

    implementation(libs.hikari)

    testImplementation(libs.h2)
    testImplementation(libs.hikari)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)

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