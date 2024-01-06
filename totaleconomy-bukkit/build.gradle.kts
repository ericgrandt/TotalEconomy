plugins {
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "com.ericgrandt"
version = "0.10.0"

repositories {
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    implementation(project(":totaleconomy-common"))
    implementation("com.zaxxer:HikariCP:5.0.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.mybatis:mybatis:3.5.11")

    testImplementation("com.github.MilkBowl:VaultAPI:1.7")
    testImplementation("com.h2database:h2:2.2.222")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.0")
    testImplementation("org.mybatis:mybatis:3.5.11")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    shadowJar {
        minimize {
            exclude(project(":totaleconomy-common"))
        }
    }
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("1.20.4")
    }
}
