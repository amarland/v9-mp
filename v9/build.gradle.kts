@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.jetbrains.compose.ComposeBuildConfig
import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

group = extra["v9mp.group"] as String
version = extra["v9mp.version"] as String

kotlin {
    android()

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    // val kotlinVersion = extra["kotlin.version"] as String

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("androidx.compose.ui:ui-graphics:${ComposeBuildConfig.composeVersion}")
                implementation("dev.romainguy:pathway:0.7.0")
            }
        }
        val desktopMain by getting {
            // apply(plugin = "org.jetbrains.reflekt")

            dependencies {
                implementation(compose.ui)
                // implementation("org.jetbrains.reflekt:reflekt-dsl:$kotlinVersion")
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose.desktop.currentOs)
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
