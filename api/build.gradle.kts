import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java

    id("org.springframework.boot") version "2.2.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.2.0.RELEASE")
    implementation("org.jetbrains:annotations:17.0.0")
    implementation("com.typesafe:config:1.4.0")

    implementation(project(":commons"))
    implementation(project(":tg-client"))
    implementation(project(":postgres-db"))
    implementation(project(":drive"))
    implementation(project(":tg-to-drive"))

    compileOnly("org.projectlombok:lombok:1.18.10")
    annotationProcessor("org.projectlombok:lombok:1.18.10")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.2.0.RELEASE") {
        exclude(module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}

val bootJar = tasks.withType<BootJar> {
    archiveBaseName.set("tg-remote-client")
    archiveVersion.set(project.version as String)
    mainClassName = "kspt.orange.tg_remote_client.api.TgRemoteClientApp"
}
