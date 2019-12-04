allprojects {
    group = "kspt.orange.tg_remote_client"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
    }
}

plugins {
    java
}

tasks.register("stage") {
    dependsOn(":clean", "api:bootJar")
}
