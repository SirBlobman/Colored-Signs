import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

fun fetchProperty(propertyName: String, defaultValue: String): String {
    val found = findProperty(propertyName)
    if (found != null) {
        return found.toString()
    }

    return defaultValue
}

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven("https://nexus.sirblobman.xyz/private/") {
        credentials {
            username = rootProject.ext.get("mavenUsername") as String
            password = rootProject.ext.get("mavenPassword") as String
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
    named<Jar>("jar") {
        enabled = false
    }

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("ColoredSigns")
        archiveClassifier.set(null as String?)
        version = rootProject.ext.get("calculatedVersion") as String
    }

    named("build") {
        dependsOn("shadowJar")
    }

    processResources {
        val pluginName = fetchProperty("bukkit.plugin.name", "")
        val pluginPrefix = fetchProperty("bukkit.plugin.prefix", "")
        val pluginDescription = fetchProperty("bukkit.plugin.description", "")
        val pluginWebsite = fetchProperty("bukkit.plugin.website", "")
        val pluginMainClass = fetchProperty("bukkit.plugin.main", "")
        val pluginVersion = rootProject.ext.get("calculatedVersion")

        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "pluginName" to pluginName,
                    "pluginPrefix" to pluginPrefix,
                    "pluginDescription" to pluginDescription,
                    "pluginWebsite" to pluginWebsite,
                    "pluginMainClass" to pluginMainClass,
                    "pluginVersion" to pluginVersion
                )
            )
        }
    }
}
