import org.jreleaser.model.Active
import java.time.LocalDate

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jReleaser)
    kotlin(libs.plugins.kotlinSerialization.get().pluginId) version libs.plugins.kotlinSerialization.get().version.toString()
    id("kotlin-parcelize")
    id("maven-publish")
}

android {
    namespace = "com.ehsanmsz.kiwanolog"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.kotlinSerialization)
    implementation(libs.navigation.compose)
    implementation(libs.paging.compose)
    implementation(libs.compose.constraintLayout)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    implementation(libs.roomPaging)
    ksp(libs.roomCompiler)

    implementation(libs.ktorClientCore)
}


/**
 * Publish
 */
val publishVersion = "0.1.0"

val sourceJar by tasks.creating(Jar::class) {
    archiveClassifier.set("source")
    from("src/main/java")
}

artifacts {
    archives(sourceJar)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.ehsanmsz"
                artifactId = "kiwano-log"
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
                artifact(sourceJar)
            }
        }

        repositories {
            maven {
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
            active.set(Active.ALWAYS)
            url.set("https://central.sonatype.com/api/v1/publisher")
            sign.set(true)
            sourceJar.set(true)
            stagingRepository("kiwano-log/build/staging-deploy")
        }
    }
    release.github {
        skipRelease.set(true)
        skipTag.set(true)
    }
}
