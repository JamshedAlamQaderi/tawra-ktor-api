plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

ksp {
    arg("jvm-output-path", "$buildDir/generated/tawraapi/jvm/jvmMain/kotlin/")
    arg("common-output-path", "$buildDir/generated/tawraapi/metadata/commonMain/kotlin/")
}

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
            kotlin.srcDir("$buildDir/generated/tawraapi/metadata/commonMain/kotlin/")
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("$buildDir/generated/tawraapi/jvm/jvmMain/kotlin/")
            dependencies {
                implementation(project(":ksp-processor"))
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:1.4.5")
                implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
            }
        }
        val jvmTest by getting
    }
}

dependencies {
    add("kspJvm", project(":ksp-processor"))
}