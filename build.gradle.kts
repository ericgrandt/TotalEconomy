plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    checkstyle
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("com.github.johnrengelman.shadow")

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
        }
        jar {
            enabled = false // No need for a non-shadow jar, so just disable it
        }
        build {
            dependsOn(shadowJar)
        }
    }
}
//
//tasks.getByName<Test>("test") {
//    useJUnitPlatform()
//}
//
//tasks.shadowJar {
//    archiveFileName.set("TotalEconomy-${version}.jar")
//    dependencies {
//        include(dependency("com.zaxxer:HikariCP"))
//    }
//}
//
//tasks.jar {
//    enabled = false
//}
//
//tasks.build {
//    dependsOn(tasks.shadowJar)
//}
//
//tasks.runServer {
//    dependsOn(tasks.shadowJar)
//    minecraftVersion("1.20.4")
//}