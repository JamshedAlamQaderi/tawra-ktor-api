plugins{
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.serialization) apply false
}

subprojects {
    group = "com.jamshedalamqaderi"
    version = "0.0.1-SNAPSHOT"
    apply {
        plugin(rootProject.libs.plugins.multiplatform.get().pluginId)
        plugin(rootProject.libs.plugins.kspPlugin.get().pluginId)
        plugin(rootProject.libs.plugins.serialization.get().pluginId)
    }
}