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
    }
    versionCatalogs {
        create("libs"){
            from(files("./gradle/dependencies.toml"))
        }
    }
}
rootProject.name = "tawra-ktor-api-project"

includeBuild("gradle-plugin"){
//    dependencySubstitution {
//        substitute(module("com.jamshedalamqaderi:tawra-ktor-api-plugin"))
//    }
}
include(":ksp-processor")
include(":example")
