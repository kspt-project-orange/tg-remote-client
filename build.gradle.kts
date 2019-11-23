allprojects {
    group = "kspt.orange.tg_remote_client"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
        maven(url= "https://repo.spring.io/milestone")
    }
}

plugins {
    java
}

tasks.register("stage") {
    dependsOn(":clean", "api:bootJar")
}
