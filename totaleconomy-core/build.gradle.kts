plugins {
    kotlin("jvm")
}

group = "com.ericgrandt.totaleconomy"
version = "0.15.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.kyori:adventure-api:5.1.1")
    implementation(project(":totaleconomy-api", configuration = "shadow"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}
