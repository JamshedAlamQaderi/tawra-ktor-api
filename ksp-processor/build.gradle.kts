plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

val kspVersion: String by project
val kotlinPoetVersion: String by project
val ktorVersion: String by project
val korioVersion: String by project

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                api("com.soywiz.korlibs.korio:korio:$korioVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
                implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
                implementation("com.squareup:kotlinpoet-ksp:$kotlinPoetVersion")
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
            }
        }
        val jvmTest by getting
    }
}

