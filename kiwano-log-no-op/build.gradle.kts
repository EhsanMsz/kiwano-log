import org.jreleaser.model.Active

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jReleaser)
    id("kotlin-parcelize")
    id("maven-publish")
}

android {
    namespace = "com.ehsanmsz.kiwanolog"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.ktorClientCore)
}

/**
 * Publish
 */
val publishVersion = "0.1.0"

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.ehsanmsz"
                artifactId = "kiwano-log-no-op"
                version = publishVersion

                pom {
                    name.set(project.name)
                    description.set("Ktor client android logger")
                    url.set("https://github.com/EhsanMsz/kiwano-log")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            name.set("Ehsan Msz")
                            email.set("contact@ehsanmsz.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:github.com/EhsanMsz/kiwano-log.git")
                        url.set("https://github.com/EhsanMsz/kiwano-log")
                        developerConnection.set("scm:git:ssh://git@github.com:EhsanMsz/kiwano-log.git")
                    }
                }
            }
        }

        repositories {
            maven {
                name = "LocalRepo"
                url = uri(layout.buildDirectory.dir("staging-deploy"))
            }
        }
    }
}

jreleaser {
    gitRootSearch.set(true)
    project.version.set(publishVersion)
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
        verify.set(true)
    }
    deploy.maven.mavenCentral {
        create("sonatype") {
            applyMavenCentralRules.set(false)
            active.set(Active.ALWAYS)
            url.set("https://central.sonatype.com/api/v1/publisher")
            sign.set(true)
            stagingRepository("build/staging-deploy")
        }
    }
    release.github {
        skipRelease.set(true)
        skipTag.set(true)
    }
}