// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    kotlin("kapt") version "1.9.0"
    id("com.google.dagger.hilt.android") version "2.48" apply false
    // id("androidx.navigation.safeargs") version "2.7.7" apply false
    kotlin("plugin.serialization") version "1.8.0"
    id("com.google.gms.google-services") version "4.4.0" apply false

}