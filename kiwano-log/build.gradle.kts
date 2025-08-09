import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    kotlin(libs.plugins.kotlinSerialization.get().pluginId) version libs.plugins.kotlinSerialization.get().version.toString()
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
val publishGroupId = "com.ehsanmsz"
val publishVersion = "0.1.0"
val publishArtifactId = "kiwano-log-no-op"

val sourceJar by tasks.creating(Jar::class) {
    archiveClassifier.set("source")
    from("src/main/java")
}

artifacts {
    archives(sourceJar)
}

group = publishGroupId
version = publishVersion

var ossrhUsername = ""
var ossrhPassword = ""

val file = rootProject.file("local.properties")
var isLocalPropertiesAvailable = file.exists()

if (isLocalPropertiesAvailable) {
    val properties = Properties().apply { load(FileInputStream(file)) }
    ossrhUsername = properties["ossrhUsername"] as String
    ossrhPassword = properties["ossrhPassword"] as String
} else {
    ossrhUsername = System.getenv("OSSRH_USERNAME")
    ossrhPassword = System.getenv("OSSRH_PASSWORD")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = publishGroupId
                version = publishVersion
                artifactId = publishArtifactId

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
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }

    signing {
        if (!isLocalPropertiesAvailable) {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(publishing.publications)
    }
}
