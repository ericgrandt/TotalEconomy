import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin") version "2.2.0"
}

var spongeApiVersion = "12.0.0-SNAPSHOT"

sponge {
    apiVersion(spongeApiVersion)
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("totaleconomy") {
        displayName("Total Economy")
        entrypoint("${project.group}.TotalEconomy")
        description("All in one economy plugin for Minecraft")
        links {
            homepage("https://github.com/ericgrandt/TotalEconomy")
            source("https://github.com/ericgrandt/TotalEconomy")
            issues("https://github.com/ericgrandt/TotalEconomy/issues")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

dependencies {
    implementation(project(":totaleconomy-common", configuration = "shadow"))

    runtimeOnly("com.mysql:mysql-connector-j:8.2.0")

    testImplementation("org.spongepowered:spongeapi:${spongeApiVersion}")
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("com.mysql:mysql-connector-j:.*"))
        }

        mergeServiceFiles()

        // Exclude net.kyori packages to avoid interfering with the one bundled with Sponge
        exclude("net/kyori/**/*")
    }
}