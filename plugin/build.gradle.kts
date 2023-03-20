import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    maven {
        url = uri("https://nexus.sirblobman.xyz/private/")

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
    implementation(project(path = ":abstract", configuration = "archives"))
    implementation(project(path = ":legacy", configuration = "archives"))
    implementation(project(path = ":modern", configuration = "archives"))
    implementation(project(path = ":editor", configuration = "archives"))

    // Bukkit API
    compileOnly("org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        val pluginName = (findProperty("bukkit.plugin.name") ?: "") as String
        val pluginPrefix = (findProperty("bukkit.plugin.prefix") ?: "") as String
        val pluginDescription = (findProperty("bukkit.plugin.description") ?: "") as String
        val pluginWebsite = (findProperty("bukkit.plugin.website") ?: "") as String
        val pluginMainClass = (findProperty("bukkit.plugin.main") ?: "") as String
        val pluginVersion = rootProject.ext.get("calculatedVersion")

        filesMatching("plugin.yml") {
            expand(mapOf(
                "pluginName" to pluginName,
                "pluginPrefix" to pluginPrefix,
                "pluginDescription" to pluginDescription,
                "pluginWebsite" to pluginWebsite,
                "pluginMainClass" to pluginMainClass,
                "pluginVersion" to pluginVersion
            ))
        }
    }

    named<Jar>("jar") {
        enabled = false
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
