val mavenUsername = fetchEnv("MAVEN_DEPLOY_USR", "mavenUsernameSirBlobman", "")
val mavenPassword = fetchEnv("MAVEN_DEPLOY_PSW", "mavenPasswordSirBlobman", "")
rootProject.ext.set("mavenUsername", mavenUsername)
rootProject.ext.set("mavenPassword", mavenPassword)

val baseVersion = fetchProperty("version.base", "invalid")
val betaString = fetchProperty("version.beta", "false")
val jenkinsBuildNumber = fetchEnv("BUILD_NUMBER", null, "Unofficial")

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
val calculatedVersion = "$baseVersion.$betaVersion$jenkinsBuildNumber"
rootProject.ext.set("calculatedVersion", calculatedVersion)

fun fetchProperty(propertyName: String, defaultValue: String): String {
    val found = findProperty(propertyName)
    if (found != null) {
        return found.toString()
    }

    return defaultValue
}

fun fetchEnv(envName: String, propertyName: String?, defaultValue: String): String {
    val found = System.getenv(envName)
    if (found != null) {
        return found
    }

    if (propertyName != null) {
        return fetchProperty(propertyName, defaultValue)
    }

    return defaultValue
}

plugins {
    id("java")
}

tasks.named("jar") {
    enabled = false
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-Xlint:deprecation")
            options.compilerArgs.add("-Xlint:unchecked")
        }

        withType<Javadoc> {
            options.encoding = "UTF-8"
            val standardOptions = options as StandardJavadocDocletOptions
            standardOptions.addStringOption("Xdoclint:none", "-quiet")
        }
    }
}
