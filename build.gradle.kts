plugins {
    java
}

repositories {
    mavenCentral()
}

subprojects {
    plugins.apply("java")

    repositories {
        mavenCentral()
    }
}
