allprojects {
    val project = this

    group = "kspt.orange.tg_remote_client"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
    }

    plugins.withType(JavaPlugin::class) {
        dependencies {
            if (project != project(":api")) {
                implementation("io.projectreactor:reactor-core:3.3.2.RELEASE")
                implementation("org.slf4j:slf4j-api:1.7.30")
            }

            implementation("org.jetbrains:annotations:19.0.0")
            implementation("com.typesafe:config:1.4.0")

            compileOnly("org.projectlombok:lombok:1.18.12")
            annotationProcessor("org.projectlombok:lombok:1.18.12")
        }
    }
}

plugins {
    java
}

tasks.register("stage") {
    dependsOn(":clean", "api:bootJar")
}
