plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    checkstyle
}

group = "com.ericgrandt"
version = "0.10.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    testImplementation("com.github.MilkBowl:VaultAPI:1.7")

    implementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("com.h2database:h2:2.2.222")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveFileName.set("TotalEconomy-${version}.jar")
    dependencies {
        include(dependency("com.zaxxer:HikariCP"))
    }
}

tasks.jar {
    enabled = false
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.runServer {
    dependsOn(tasks.shadowJar)
    minecraftVersion("1.20.4")
}