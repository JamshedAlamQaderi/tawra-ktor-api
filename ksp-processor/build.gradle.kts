@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform)
}


group = "com.jamshedalamqaderi"
version = "0.0.1-SNAPSHOT"

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
                api(libs.korio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ksp)
                implementation(libs.kotlinPoet)
                implementation(libs.kotlinPoetKsp)
                implementation(libs.ktorServerCore)
                implementation(libs.ktorServerNetty)
            }
        }
        val jvmTest by getting
    }
}

