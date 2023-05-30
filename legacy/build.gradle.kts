repositories {
    maven("https://nexus.sirblobman.xyz/private/") {
        credentials {
            username = rootProject.ext.get("mavenUsername") as String
            password = rootProject.ext.get("mavenPassword") as String
        }
    }
}

dependencies {
    compileOnly(project(":abstract"))
    compileOnly("org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT")
}
