plugins {
    kotlin("jvm")
}

group = "com.ericgrandt.totaleconomy"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.kyori:adventure-api:5.1.1")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}
