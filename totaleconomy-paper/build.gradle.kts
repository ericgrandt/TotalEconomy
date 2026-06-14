plugins {
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

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.vault)

    implementation(project(":totaleconomy-api"))
    implementation(project(":totaleconomy-core"))
    implementation(libs.kotlinx.coroutines)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.kotlinx.coroutines)
    testImplementation(libs.mockk)
    testImplementation(libs.paper)
}

tasks {
    test {
        enabled = false
    }

    runServer {
        dependsOn(shadowJar)
        minecraftVersion("26.1.2")
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("TotalEconomyPaper-${project.version}.jar")

        mergeServiceFiles()

        // Exclude net.kyori packages to avoid interfering with the one bundled with Paper
        exclude("net/kyori/**/*")

        minimize {
            exclude(project(":totaleconomy-core"))
            exclude(project(":totaleconomy-api"))
        }

        // Relocate libraries to plugin package
        relocate("com.zaxxer.hikari", "com.ericgrandt.totaleconomy.libs.hikari")
        relocate("org.jetbrains.exposed", "com.ericgrandt.totaleconomy.libs.jetbrains.exposed")
    }

    jar {
        enabled = false
    }
}
