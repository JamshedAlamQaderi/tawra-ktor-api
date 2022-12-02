package com.jamshedalamqaderi.tawraktorapi

import org.gradle.api.Plugin
import org.gradle.api.Project

class TawraKtorApiPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("formatTawraKtorApi"){
            doFirst {
                println("Hello, world!")
            }
        }
    }
}