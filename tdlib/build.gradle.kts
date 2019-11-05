plugins {
    java
}

dependencies {
    implementation(files("libs/libtdjni.so"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
