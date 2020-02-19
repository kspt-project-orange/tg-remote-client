plugins {
    java
}

dependencies {
    implementation("io.r2dbc:r2dbc-postgresql:0.8.1.RELEASE")
    implementation("io.r2dbc:r2dbc-pool:0.8.1.RELEASE")

    implementation(project(":commons"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
