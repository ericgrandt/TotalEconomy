plugins {
    java
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.shadow) apply false
}

repositories {
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

subprojects {
    plugins.apply("java")

    repositories {
        mavenCentral()
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }
}
