import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java

    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux:2.2.4.RELEASE")

    implementation(project(":commons"))
    implementation(project(":tg-client"))
    implementation(project(":postgres-db"))
    implementation(project(":drive"))
    implementation(project(":tg-to-drive"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_13
}

val bootJar = tasks.withType<BootJar> {
    archiveBaseName.set("tg-remote-client")
    archiveVersion.set(project.version as String)
    mainClassName = "kspt.orange.tg_remote_client.api.TgRemoteClientApp"
}
