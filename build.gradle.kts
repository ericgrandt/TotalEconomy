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

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks {
        shadowJar {
            archiveClassifier.set("")
            archiveFileName.set("TotalEconomy-${project.version}.jar")

            minimize {
                exclude(project(":totaleconomy-common"))
            }
        }
        jar {
            enabled = false
        }
        build {
            dependsOn(shadowJar)
        }
        test {
            useJUnitPlatform()
        }
    }
}
