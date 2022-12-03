plugins {
    id("com.jamshedalamqaderi.tawra-ktor-api-plugin")
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
            kotlin.srcDir("$buildDir/generated/tawraapi/metadata/commonMain/kotlin/")
            dependencies {
                implementation(libs.ktorClientCore)
                implementation("com.jamshedalamqaderi:tawra-ktor-api")
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
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerNetty)
                implementation(libs.logback)
                implementation(libs.ktorServerContentNegotiation)
                implementation(libs.ktorSerializationJvm)
            }
        }
        val jvmTest by getting
    }
}