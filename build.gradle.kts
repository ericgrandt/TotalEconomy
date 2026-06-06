plugins {
    alias(libs.plugins.kotlin.jvm)
    // alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

subprojects {
    plugins.apply("kotlin")

    repositories {
        mavenCentral()
    }

    kotlin {
        jvmToolchain(25)
    }

    // tasks {
    //    shadowJar {
    //        archiveClassifier.set("")
    //        archiveFileName.set("TotalEconomy-${project.version}.jar")

    //        minimize {
    //            exclude(project(":totaleconomy-core"))
    //            exclude(project(":totaleconomy-api"))
    //        }
    //    }
    //    jar {
    //        enabled = true
    //    }
    //    build {
    //        // dependsOn(shadowJar)
    //    }
    //    test {
    //        useJUnitPlatform()
    //    }
    // }
}
