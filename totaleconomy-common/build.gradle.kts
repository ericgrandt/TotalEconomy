dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.mybatis:mybatis:3.5.11")
    implementation("net.kyori:adventure-api:4.15.0")
    testImplementation("org.assertj:assertj-core:3.25.1")
}
