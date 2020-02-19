plugins {
    java
}

dependencies {
    implementation(project(":commons"))

    implementation(files("libs/libtdjni.so"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
