plugins {
    kotlin("jvm")
}

group = "com.ericgrandt.totaleconomy"
version = "0.15.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("net.kyori:adventure-api:5.1.1")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation(project(":totaleconomy-api", configuration = "shadow"))

    implementation("org.jetbrains.exposed:exposed-core:1.3.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.3.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:1.3.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}
