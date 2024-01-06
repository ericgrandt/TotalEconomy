plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    checkstyle
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("com.github.johnrengelman.shadow")
    plugins.apply("checkstyle")

    repositories {
        mavenCentral()
    }

    tasks {
        shadowJar {
            archiveClassifier.set("")
            archiveFileName.set("TotalEconomy-${project.version}.jar")
        }
        jar {
            enabled = false // No need for a non-shadow jar, so just disable it
        }
        build {
            dependsOn(shadowJar)
        }
    }
}
