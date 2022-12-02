val kspVersion: String = "1.7.21-1.0.8"
val kotlinPoetVersion: String = "1.12.0"
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

