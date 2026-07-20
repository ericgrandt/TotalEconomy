plugins {
    java
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "vault"
        url = uri("https://jitpack.io")
    }
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.vault)

    implementation(project(":totaleconomy-api"))
    implementation(project(":totaleconomy-core"))

    testImplementation(libs.h2)
    testImplementation(libs.hikari)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.paper)
    testImplementation(libs.vault)

    mockitoAgent(libs.mockito.core) { isTransitive = false }

    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks {
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("26.1.2")
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("TotalEconomyPaper-${project.version}.jar")

        mergeServiceFiles()

        // Relocate libraries to plugin package
        relocate("com.zaxxer.hikari", "com.ericgrandt.totaleconomy.libs.hikari")

        exclude("net/kyori/**/*")

        minimize {
            exclude(project(":totaleconomy-core"))
            exclude(project(":totaleconomy-api"))
        }
    }

    jar {
        enabled = false
    }

    test {
        useJUnitPlatform()

        doFirst {
            jvmArgs("-javaagent:${mockitoAgent.singleFile.absolutePath}")
        }
    }
}
