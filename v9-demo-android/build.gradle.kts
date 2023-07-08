@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

val composeVersion = extra["compose.version"] as String

android {
    namespace = "com.amarland.v9mp.demo.android"

    compileSdk = (extra["android.compileSdk"] as String).toInt()

    defaultConfig {
        minSdk = (extra["android.minSdk"] as String).toInt()

        applicationId = "com.amarland.v9mp.demo"
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()

    buildFeatures.compose = true

    composeOptions.kotlinCompilerExtensionVersion = composeVersion
}

dependencies {
    implementation(project(":v9-demo-common"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
}
