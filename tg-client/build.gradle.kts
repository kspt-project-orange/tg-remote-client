plugins {
    java
}

dependencies {
    implementation("org.jetbrains:annotations:17.0.0")
    implementation("io.projectreactor:reactor-core:3.3.1.RELEASE")
    implementation("com.typesafe:config:1.4.0")
    implementation("org.slf4j:slf4j-api:1.7.25")

    compileOnly("org.projectlombok:lombok:1.18.10")
    annotationProcessor("org.projectlombok:lombok:1.18.10")

    implementation(files("libs/libtdjni.so"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}
