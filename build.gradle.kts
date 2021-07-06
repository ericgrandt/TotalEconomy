import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.PluginDependency

plugins {
    `java-library`
    id("org.spongepowered.gradle.plugin") version "1.0.3"
    id("com.github.johnrengelman.shadow") version "4.0.4"
    checkstyle
}

group = "com.erigitic"
version = "2.0.0"

repositories {
    mavenCentral()
}

sponge {
    apiVersion("8.0.0")
    plugin("totaleconomy") {
        loader(PluginLoaders.JAVA_PLAIN)
        displayName("Total Economy")
        mainClass("com.ericgrandt.TotalEconomy")
        description("All in one economy plugin for Minecraft and Sponge")
        links {
            homepage("https://github.com/Erigitic/TotalEconomy")
            source("https://github.com/Erigitic/TotalEconomy")
            issues("https://github.com/Erigitic/TotalEconomy/issues")
        }
        contributor("Erigitic") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

val javaTarget = 8 // Sponge targets a minimum of Java 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
        if (JavaVersion.current().isJava10Compatible) {
            release.set(javaTarget)
        }
    }
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

dependencies {
    implementation("org.mybatis:mybatis:3.5.6")

    testImplementation("org.mybatis:mybatis:3.5.6")
    testImplementation("org.spongepowered:spongeapi:8.0.0-SNAPSHOT")
    testImplementation("com.h2database:h2:1.3.148")
    testImplementation("com.zaxxer:HikariCP:2.6.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    testImplementation("org.mockito:mockito-core:3.6.28")
    testImplementation("org.mockito:mockito-junit-jupiter:3.6.28")
}

tasks.test {
    dependsOn("cleanTest")

    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.shadowJar {
    archiveBaseName.set("TotalEconomy")
    dependencies {
        include(dependency("org.mybatis:mybatis"))
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
}