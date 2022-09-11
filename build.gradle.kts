group = extra["v9mp.group"] as String
version = extra["v9mp.version"] as String

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // maven("https://packages.jetbrains.team/maven/p/reflekt/reflekt")
    }
}

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
    // id("org.jetbrains.reflekt") apply false
}
