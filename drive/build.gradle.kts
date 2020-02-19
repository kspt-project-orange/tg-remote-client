plugins {
    java
}

dependencies {
    implementation("com.google.api-client:google-api-client:1.23.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev110-1.23.0")

    implementation(project(":commons"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
