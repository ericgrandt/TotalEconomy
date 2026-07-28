plugins {
    id("java")
}

group = "com.ericgrandt.totaleconomy"
version = "1.1.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":totaleconomy-api"))

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}