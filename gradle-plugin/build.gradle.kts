plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.1.0"
}

pluginBundle{
    website = "https://jamshedalamqaderi.com"
    vcsUrl = "https://github.com/JamshedAlamQaderi/tawra-ktor-api"
    tags = listOf("ktor", "ktor-server", "ktor-client", "kotlin-multiplatform", "tawra-ktor-api")
}

gradlePlugin {
    plugins {
        create("tawra-ktor-api"){
            id = "com.jamshedalamqaderi.tawra-ktor-api"
            implementationClass = "com.jamshedalamqaderi.tawraktorapi.TawraKtorApiPluginKt"
            displayName = "Tawra Ktor Api"
            description = "A plugin for tawra-ktor-api library which generate client side code for ktor server"
        }
    }
}

dependencies {

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}