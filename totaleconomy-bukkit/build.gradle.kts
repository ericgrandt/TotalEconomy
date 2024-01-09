plugins {
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation(project(":totaleconomy-common", configuration = "shadow"))

    testImplementation("com.github.MilkBowl:VaultAPI:1.7")
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.20.4")
    }
}
