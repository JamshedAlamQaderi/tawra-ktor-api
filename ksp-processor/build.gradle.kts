@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
    alias(libs.plugins.dokka)
    `java-library`
    `maven-publish`
    signing
}

val projectVersion: String? by project

group = "com.jamshedalamqaderi"
version = projectVersion?.replaceFirst("v", "") ?: "0.0.1-SNAPSHOT"

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

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory.get())
}

publishing {
    val sonatypeUsername: String? by project
    val sonatypePassword: String? by project
    repositories {
        maven {
            name = "SonatypeRepository"
            val releasesRepoUrl =
                uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl =
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
    publications {
        withType<MavenPublication> {
            group = "com.jamshedalamqaderi"
            artifactId = "tawra-ktor-api"
            version = projectVersion
            artifact(javadocJar)
            pom {
                name.set("Tawra Ktor Api")
                description.set("An Multiplatform Ktor client code generator from Ktor Server Implementation wrapper")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                url.set("https://github.com/JamshedAlamQaderi/tawra-ktor-api")
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/JamshedAlamQaderi/tawra-ktor-api/issues")
                }
                scm {
                    connection.set("scm:git:git:github.com/JamshedAlamQaderi/tawra-ktor-api.git")
                    developerConnection.set("scm:git:ssh://github.com/JamshedAlamQaderi/tawra-ktor-api.git")
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
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}
