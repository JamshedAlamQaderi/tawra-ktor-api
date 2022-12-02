plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.pluginPublish)
}

group = "com.jamshedalamqaderi"
version = "0.0.1-SNAPSHOT"


publishing {
    repositories {
        mavenLocal()
    }
}


pluginBundle {
    website = "https://jamshedalamqaderi.com"
    vcsUrl = "https://github.com/JamshedAlamQaderi/tawra-ktor-api"
    tags = listOf(
        "ktor",
        "ktor-server",
        "ktor-client",
        "kotlin-multiplatform",
        "tawra-ktor-api",
        "tawra"
    )
}

gradlePlugin {
    plugins {
        create("tawra-ktor-api") {
            id = "com.jamshedalamqaderi.tawra-ktor-api-plugin"
            implementationClass = "com.jamshedalamqaderi.tawraktorapi.TawraKtorApiPlugin"
            displayName = "Tawra Ktor Api"
            description =
                "A plugin for tawra-ktor-api library which generate client side code for ktor server"
        }
    }
}

dependencies {

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}