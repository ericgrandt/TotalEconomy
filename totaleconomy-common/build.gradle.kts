dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("org.mybatis:mybatis:3.5.11")

    testImplementation("com.h2database:h2:2.2.222")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}