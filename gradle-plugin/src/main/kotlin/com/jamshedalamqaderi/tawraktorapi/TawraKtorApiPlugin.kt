package com.jamshedalamqaderi.tawraktorapi

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class TawraKtorApiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.pluginManager.apply("com.google.devtools.ksp")
        project.configure<KspExtension> {
            arg("jvm-output-path", "${project.buildDir}/generated/tawraapi/jvm/jvmMain/kotlin/")
            arg(
                "common-output-path",
                "${project.buildDir}/generated/tawraapi/metadata/commonMain/kotlin/"
            )
        }
        project.afterEvaluate {
            dependencies.add("kspJvm", "com.jamshedalamqaderi:tawra-ktor-api")
        }
    }
}