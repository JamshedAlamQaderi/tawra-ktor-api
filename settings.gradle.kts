pluginManagement {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencyResolutionManagement {
        val kspVersion: String by settings
        val kotlinVersion: String by settings
        plugins {
            id("com.google.devtools.ksp") version kspVersion
            kotlin("multiplatform") version kotlinVersion
            kotlin("plugin.serialization") version kotlinVersion
        }
        repositories {
            google()
            mavenCentral()
        }
    }
}

rootProject.name = "tawra-ktor-api"

include(":ksp-processor")
include(":demo")