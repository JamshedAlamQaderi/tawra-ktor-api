buildscript {
    dependencies {
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.7.21-1.0.8")
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
}
val projectVersion: String? by project

group = "com.jamshedalamqaderi"
version = projectVersion?.replaceFirst("v", "") ?: "0.0.1-SNAPSHOT"

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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.7.21-1.0.8")
    implementation("com.facebook:ktfmt:0.41")
    testImplementation(gradleTestKit())
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
