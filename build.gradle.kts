@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.serialization) apply false
}

subprojects {
    group = "com.jamshedalamqaderi"
    version = rootProject.libs.versions.tawraKtorApiVersion
    apply {
        plugin(rootProject.libs.plugins.multiplatform.get().pluginId)
        plugin(rootProject.libs.plugins.serialization.get().pluginId)
    }
}