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
    compileOnly(project(":totaleconomy-api"))
    implementation(project(":common"))
    implementation(project(":totaleconomy-jobs-core"))

    mockitoAgent(libs.mockito.core) { isTransitive = false }

    testImplementation(libs.paper)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

tasks {
    runServer {
        dependsOn(shadowJar)
        minecraftVersion("26.2")
    }

    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("TotalEconomyJobsPaper-${project.version}.jar")

        mergeServiceFiles()

        // Exclude the API so it falls back to using the TotalEconomy bundled API
        exclude("com/ericgrandt/totaleconomy/api/**")

        minimize {
            exclude(project(":totaleconomy-jobs-core"))
        }
    }

    test {
        useJUnitPlatform()
    }
}