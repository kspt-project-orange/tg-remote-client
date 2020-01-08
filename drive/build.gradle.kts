plugins {
    java
}

dependencies {
    implementation("org.jetbrains:annotations:17.0.0")
    implementation("com.typesafe:config:1.4.0")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("io.projectreactor:reactor-core:3.3.1.RELEASE")

    implementation("com.google.api-client:google-api-client:1.23.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev110-1.23.0")

    implementation(project(":commons"))

    compileOnly("org.projectlombok:lombok:1.18.10")
    annotationProcessor("org.projectlombok:lombok:1.18.10")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
