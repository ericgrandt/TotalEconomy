plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.0"
    checkstyle
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("com.gradleup.shadow")
    plugins.apply("checkstyle")

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("com.h2database:h2:2.2.224")
        testImplementation("com.zaxxer:HikariCP:5.1.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
        testImplementation("org.mockito:mockito-core:5.12.0")
        testImplementation("org.mockito:mockito-inline:5.2.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks {
        shadowJar {
            archiveClassifier.set("")
            archiveFileName.set("TotalEconomy-${project.version}.jar")

            mergeServiceFiles()

            // Exclude net.kyori packages to avoid interfering with the one bundled with Sponge
            exclude("net/kyori/**/*")

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
