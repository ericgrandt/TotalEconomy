plugins {
    id("java")
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    compileOnly(libs.paper)

    implementation(project(":api"))
    implementation(project(":common"))
    implementation(project(":jobs:totaleconomy-jobs"))

    testImplementation(libs.h2)
    testImplementation(libs.hikari)
    testImplementation(libs.paper)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(project(":totaleconomy"))

    mockitoAgent(libs.mockito.core) { isTransitive = false }

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    runServer {
        dependsOn(shadowJar)
        minecraftVersion(libs.versions.minecraft.get())
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("TotalEconomyJobsPaper-${project.version}.jar")

        mergeServiceFiles()

        // Exclude the API so it falls back to using the TotalEconomy bundled API
        exclude("com/ericgrandt/totaleconomy/api/**")

        minimize {
            exclude(project(":jobs:totaleconomy-jobs"))
        }
    }

    jar {
        enabled = false
    }

    test {
        useJUnitPlatform()

        doFirst {
            jvmArgs("-javaagent:${mockitoAgent.singleFile.absolutePath}")
        }
    }
}