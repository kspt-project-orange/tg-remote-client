plugins {
    java
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":tg-client"))
    implementation(project(":postgres-db"))
    implementation(project(":drive"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
