@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    `java-library`
    `maven-publish`
    signing
}

group = "com.jamshedalamqaderi"
version = libs.versions.tawraKtorApiVersion

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
                implementation(libs.ktorServerWebsocket)
            }
        }
        val jvmTest by getting
    }
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "SonatypeRepository"
            val releasesRepoUrl =
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl =
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }
    publications {
        withType<MavenPublication> {
            group = "com.jamshedalamqaderi"
            artifactId = "tawra-ktor-api"
            artifact(javadocJar.get())
            pom {
                name.set("Tawra Ktor Api")
                description.set("An Multiplatform Ktor client code generator from Ktor Server Implementation wrapper")
                licenses {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
                url.set("https://github.com/JamshedAlamQaderi/tawra-ktor-api")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/JamshedAlamQaderi/tawra-ktor-api/issues")
                }
                scm {
                    connection.set("https://github.com/JamshedAlamQaderi/tawra-ktor-api.git")
                    url.set("https://github.com/JamshedAlamQaderi/tawra-ktor-api")
                }
                developers {
                    developer {
                        name.set("Jamshed Alam Qaderi")
                        id.set("JamshedAlamQaderi")
                        email.set("jamshedalamqaderi@gmail.com")
                        url.set("https://jamshedalamqaderi.com")
                        timezone.set("UTC+06:00")
                    }
                }
            }
        }
    }
}


signing {
    useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PASSWORD"))
    sign(publishing.publications)
}
