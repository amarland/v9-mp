pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt")
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        // id("org.jetbrains.reflekt").version(extra["kotlin.version"] as String)
    }
}

rootProject.name = "v9-mp"

include(":v9", ":v9-demo-common", ":v9-demo-android", ":v9-demo-desktop")
