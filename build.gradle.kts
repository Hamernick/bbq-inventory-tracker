plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.0" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
