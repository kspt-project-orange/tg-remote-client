plugins {
    java
}

dependencies {
    implementation("org.jetbrains:annotations:17.0.0")
    //TODO: add hybernate and postgres dependencies

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
