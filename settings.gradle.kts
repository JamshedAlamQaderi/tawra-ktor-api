pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("./gradle/dependencies.toml"))
        }
    }
}
rootProject.name = "tawra-ktor-api-project"

include(":example")
includeBuild("./gradle-plugin")
includeBuild("./ksp-processor")
