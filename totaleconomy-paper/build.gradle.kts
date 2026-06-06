plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
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
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    implementation(project(":totaleconomy-core", configuration = "shadow"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    testImplementation("io.mockk:mockk:1.14.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation(project(":totaleconomy-core"))
}

// tasks {
//    runServer {
//        dependsOn(shadowJar)
//        minecraftVersion("1.21.4")
//    }
//
//    shadowJar {
//        mergeServiceFiles()
//
//        // Exclude net.kyori packages to avoid interfering with the one bundled with Paper
//        exclude("net/kyori/**/*")
//    }
// }
