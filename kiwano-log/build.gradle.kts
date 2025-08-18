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
jreleaser {
    gitRootSearch.set(true)
    project {
        name.set("kiwano-log")
        description.set("Ktor client android logger")
        version.set("0.1.0")
        license.set("Apache-2.0")
        author("Ehsan Msz")
        inceptionYear.set("2024")
        copyright.set("Copyright (c) ${LocalDate.now().year} Ehsan Msz")
        links {
            homepage.set("https://github.com/EhsanMsz/kiwano-log")
            documentation.set("https://github.com/EhsanMsz/kiwano-log")
            contact.set("https://ehsanmsz.com")
        }
    }
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
            javadocJar.set(true)
            stagingRepository("target/staging-deploy")
        }
    }
    release.github {
        skipRelease.set(true)
        skipTag.set(true)
    }
}
