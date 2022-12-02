plugins {
    id("com.jamshedalamqaderi.tawra-ktor-api-plugin")
}

ksp {
    arg("jvm-output-path", "$buildDir/generated/tawraapi/jvm/jvmMain/kotlin/")
    arg("common-output-path", "$buildDir/generated/tawraapi/metadata/commonMain/kotlin/")
}

val ktorVersion: String = "2.1.3"
val korioVersion: String = "2.2.0"

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
                implementation(project(":ksp-processor"))
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
    add("kspJvm", "com.jamshedalamqader:tawra-ktor-api:0.0.1-SNAPSHOT")
}