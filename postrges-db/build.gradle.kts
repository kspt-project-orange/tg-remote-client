plugins {
    java
}

dependencies {
    implementation("org.jetbrains:annotations:17.0.0")
    implementation("io.r2dbc:r2dbc-postgresql:0.8.0.RC2")
    implementation("io.r2dbc:r2dbc-pool:0.8.0.RC2")

    implementation("com.typesafe:config:1.4.0")

    compileOnly("org.projectlombok:lombok:1.18.10")
    annotationProcessor("org.projectlombok:lombok:1.18.10")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
