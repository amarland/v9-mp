@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    android()

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":v9"))
                api(compose.ui)
                api(compose.material)
            }
        }
    }
}

android {
    compileSdk = (extra["android.compileSdk"] as String).toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = (extra["android.minSdk"] as String).toInt()
        targetSdk = (extra["android.targetSdk"] as String).toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
