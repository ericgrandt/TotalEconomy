plugins {
    id("java-library")
    id("io.github.goooler.shadow") version "8.1.7"
    checkstyle
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("io.github.goooler.shadow")
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
