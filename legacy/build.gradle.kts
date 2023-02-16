plugins {
    id("java")
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
    compileOnly("org.bukkit:bukkit:1.7.10-R0.1-SNAPSHOT")
}
