plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}

subprojects {
    plugins.apply("kotlin")

    repositories {
        mavenCentral()
    }

    kotlin {
        jvmToolchain(25)
    }
}
