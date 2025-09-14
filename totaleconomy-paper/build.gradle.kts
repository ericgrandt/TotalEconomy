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
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    implementation(project(":totaleconomy-common", configuration = "shadow"))

    testImplementation("com.github.MilkBowl:VaultAPI:1.7.1")
}

tasks {
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.21.4")
    }

    shadowJar {
        mergeServiceFiles()

        // Exclude net.kyori packages to avoid interfering with the one bundled with Paper
        exclude("net/kyori/**/*")
    }
}