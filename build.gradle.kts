plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1"
    kotlin("jvm") version "2.3.21"
}

repositories {
    mavenCentral()
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("com.gradleup.shadow")
    plugins.apply("kotlin")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("com.michael-bull.kotlin-result:kotlin-result:2.3.1")

        testImplementation("com.h2database:h2:2.4.240")
        testImplementation("com.zaxxer:HikariCP:5.1.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
        testImplementation("org.mockito:mockito-core:5.12.0")
        testImplementation("org.mockito:mockito-inline:5.2.0")
        testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
        testImplementation(kotlin("test"))

        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    }

    kotlin {
        jvmToolchain(25)
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
