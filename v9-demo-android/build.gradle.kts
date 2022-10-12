@file:Suppress("UnstableApiUsage")

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.amarland.v9mp.demo.android"

    compileSdk = (extra["android.compileSdk"] as String).toInt()

    defaultConfig {
        minSdk = (extra["android.minSdk"] as String).toInt()
        targetSdk = (extra["android.targetSdk"] as String).toInt()

        applicationId = "com.amarland.v9mp.demo"
        versionCode = 1
        versionName = "1.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()

    buildFeatures.compose = true

    composeOptions.kotlinCompilerExtensionVersion = "1.3.2"
}

val composeVersion = "1.2.1"

dependencies {
    implementation(project(":v9-demo-common"))
    implementation("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
}
