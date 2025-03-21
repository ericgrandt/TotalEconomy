plugins {
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    implementation(project(":totaleconomy-common", configuration = "shadow"))

    testImplementation("com.github.MilkBowl:VaultAPI:1.7.1")
    testImplementation("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
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
