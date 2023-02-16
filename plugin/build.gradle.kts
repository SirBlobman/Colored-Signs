import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    maven {
        name = "sirblobman-private"
        url = uri("https://nexus.sirblobman.xyz/repository/private/")

        credentials {
            var currentUsername = System.getenv("MAVEN_DEPLOY_USR")
            if (currentUsername == null) {
                currentUsername = property("mavenUsernameSirBlobman") as String
            }

            var currentPassword = System.getenv("MAVEN_DEPLOY_PSW")
            if (currentPassword == null) {
                currentPassword = property("mavenPasswordSirBlobman") as String
            }

            username = currentUsername
            password = currentPassword
        }
    }
}

dependencies {
    // Module Dependencies
    implementation(project(path = ":legacy", configuration = "archives"))
    implementation(project(path = ":modern", configuration = "archives"))
    implementation(project(path = ":editor", configuration = "archives"))

    // Bukkit API
    compileOnly("org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT")
}

tasks {
    named<Jar>("jar") {
        enabled = false
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    named<ShadowJar>("shadowJar") {
        val calculatedVersion = rootProject.ext.get("calculatedVersion") as String
        archiveFileName.set("ColoredSigns-${calculatedVersion}.jar")
        archiveClassifier.set(null as String?)
    }

    build {
        dependsOn(shadowJar)
    }
}
