plugins {
    id("com.android.application") version "8.13.0" apply false
    id("com.android.library") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("com.google.devtools.ksp") version "2.2.20-2.0.3" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" apply false
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
