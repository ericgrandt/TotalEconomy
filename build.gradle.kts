plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.0"
    kotlin("jvm") version "2.2.21"
    checkstyle
}

repositories {
    mavenCentral()
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("com.gradleup.shadow")
    plugins.apply("checkstyle")
    plugins.apply("kotlin")

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
        testImplementation(kotlin("test"))

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    }

    kotlin {
        jvmToolchain(21)
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
