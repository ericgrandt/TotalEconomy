dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.mybatis:mybatis:3.5.11")
}
