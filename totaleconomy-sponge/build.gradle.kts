import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin") version "2.2.0"
}

var spongeApiVersion = "10.0.0"

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

    testImplementation("org.spongepowered:spongeapi:${spongeApiVersion}")
}
