plugins {
    id("java")
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
    test {
        useJUnitPlatform()
    }
}