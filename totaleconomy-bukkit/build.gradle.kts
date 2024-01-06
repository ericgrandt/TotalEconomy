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
    testImplementation(project(":totaleconomy-common", configuration = "shadow"))

    testImplementation("com.github.MilkBowl:VaultAPI:1.7")
    testImplementation("com.h2database:h2:2.2.222")
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.20.4")
    }
}
