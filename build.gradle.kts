import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    `java-library`
    id("org.spongepowered.gradle.plugin") version "2.0.1"
    id("com.github.johnrengelman.shadow") version "4.0.4"
    id("eclipse")
    checkstyle
}

group = "com.ericgrandt"
version = "2.0.0"

repositories {
    mavenCentral()
	jcenter()
	maven { url = uri("https://maven.blamejared.com") }
	maven { url = uri("https://jitpack.io") }
}

sponge {
    apiVersion("8.0.0")
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("totaleconomy") {
        displayName("Total Economy")
        entrypoint("com.ericgrandt.TotalEconomy")
        description("All in one economy plugin for Minecraft and Sponge")
        links {
            homepage("https://github.com/ericgrandt/TotalEconomy")
            source("https://github.com/ericgrandt/TotalEconomy")
            issues("https://github.com/ericgrandt/TotalEconomy/issues")
        }
        contributor("Eric Grandt") {
            description("Lead Developer")
        }
        contributor("SawFowl") {
            description("Various code edits")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
        dependency("localeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            version("2.1.0")
            optional(true)
        }
    }
}

val javaTarget = 8 // Sponge targets a minimum of Java 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

dependencies {
    implementation("org.mybatis:mybatis:3.5.6")
    implementation("com.github.SawFowl:LocaleAPI:2.1.0")
    implementation("mysql:mysql-connector-java:8.0.29")

    testImplementation("org.mybatis:mybatis:3.5.6")
    testImplementation("org.spongepowered:spongeapi:8.0.0")
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
        exclude(dependency("com.github.SawFowl:LocaleAPI:2.1.0"))
        include(dependency("mysql:mysql-connector-java:8.0.29"))
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

tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}
