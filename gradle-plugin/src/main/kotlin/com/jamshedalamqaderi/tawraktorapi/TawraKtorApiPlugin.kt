package com.jamshedalamqaderi.tawraktorapi

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

class TawraKtorApiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension = project.extensions.create<TawraKtorApiExtension>("tawraKtorApi")
        extension.packageName.convention("tawraktorapi")

        project.pluginManager.apply("com.google.devtools.ksp")

        project.afterEvaluate {
            project.configure<KspExtension> {
                arg(
                    "jvm-output-path",
                    jvmMainOutputDir(project)
                )
                arg(
                    "common-output-path",
                    commonMainOutputDir(project)
                )
                arg("package-name", extension.packageName.get())
            }

            dependencies.add("kspJvm", TawraKtorApiDeps.tawraKtorApi)
            val kotlinExtension =
                extensions.getByName("kotlin") as org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
            val kotlinSourceSets = kotlinExtension.sourceSets
            kotlinSourceSets.getByName("commonMain") {
                kotlin.srcDir(commonMainOutputDir(this@afterEvaluate))
            }
            kotlinSourceSets.getByName("jvmMain") {
                kotlin.srcDir(jvmMainOutputDir(this@afterEvaluate))
            }
        }

        project.tasks.register("generateApi") {
            group = "tawraKtorApi"
            description = "Generate client code from Ktor server Api wrapper"
            dependsOn("kspKotlinJvm")
            dependsOn("formatGeneratedCode")
        }

        project.tasks.register("formatGeneratedCode") {
            group = "tawraKtorApi"
            description = "Format generated code by TawraKtorApi"
            doLast {
                com.facebook.ktfmt.cli.Main.main(
                    arrayOf(
                        "--kotlinlang-style",
                        "${project.buildDir}/generated/tawra-ktor-api/"
                    )
                )
            }
        }
    }

    private fun commonMainOutputDir(project: Project): String {
        return "${project.buildDir}/generated/tawra-ktor-api/metadata/commonMain/kotlin/"
    }

    private fun jvmMainOutputDir(project: Project): String {
        return "${project.buildDir}/generated/tawra-ktor-api/jvm/jvmMain/kotlin/"
    }
}
