pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs"){
            from(files("../gradle/dependencies.toml"))
        }
    }
}
rootProject.name = "tawra-ktor-api-plugin"