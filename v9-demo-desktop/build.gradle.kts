@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":v9-demo-common"))
                implementation(compose.desktop.currentOs)
            }
        }
        // val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.amarland.v9mp.demo.desktop.MainWindow"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "v9-mp-demo"
            packageVersion = "1.0.0"
        }
    }
}
