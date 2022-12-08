@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kover) apply false
}

subprojects {
    group = "com.jamshedalamqaderi"
    version = rootProject.libs.versions.tawraKtorApiVersion
    apply {
        plugin(rootProject.libs.plugins.multiplatform.get().pluginId)
        plugin(rootProject.libs.plugins.serialization.get().pluginId)
        plugin(rootProject.libs.plugins.ktlint.get().pluginId)
        plugin(rootProject.libs.plugins.kover.get().pluginId)
    }
}

fun registerTask(taskName: String, group: String = "") {
    tasks.register(taskName) {
        this.group = group
        dependsOn(":example:$taskName")
        dependsOn(gradle.includedBuild("gradle-plugin").task(":$taskName"))
        dependsOn(gradle.includedBuild("ksp-processor").task(":$taskName"))
    }
}

registerTask("clean", "tawra-ktor-api")
registerTask("ktlintFormat", "tawra-ktor-api")
registerTask("ktlintCheck", "tawra-ktor-api")
registerTask("koverXmlReport", "tawra-ktor-api")
registerTask("koverHtmlReport", "tawra-ktor-api")