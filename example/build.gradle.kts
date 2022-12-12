import com.jamshedalamqaderi.tawraktorapi.TawraKtorApiDeps

plugins {
    id("com.jamshedalamqaderi.tawra-ktor-api-plugin")
}

repositories {
    mavenCentral()
}

tawraKtorApi {
    packageName.set("com.jamshedalamqaderi.tawraktorapi")
}

ktlint {
    filter {
        exclude { it.file.path.contains("$buildDir/generated/tawra-ktor-api") }
    }
}

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
                implementation(libs.ktorClientCore)
                implementation(libs.ktorClientWebsocket)
                implementation(libs.ktorClientCio)
                implementation(TawraKtorApiDeps.tawraKtorApi)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerNetty)
                implementation(libs.logback)
                implementation(libs.ktorServerContentNegotiation)
                implementation(libs.ktorSerializationJvm)
                implementation(libs.ktorServerWebsocket)
            }
        }
        val jvmTest by getting
    }
}
