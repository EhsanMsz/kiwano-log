import java.io.FileInputStream
import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.ehsanmsz.kiwanolog"
    compileSdk = 35

    defaultConfig {
        minSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
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
val publishGroupId = "com.ehsanmsz"
val publishVersion = "0.1.0"
val publishArtifactId = "kiwano-log"

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
