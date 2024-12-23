import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

@OptIn(ExperimentalComposeLibrary::class)
kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget()
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(libs.versions.jvm.get())
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlin.bom)
                api(libs.kotlin.stdlib)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.components.resources)
            }
        }
        val commonTest by getting {
            dependencies {
                api(libs.kotlin.bom)
                api(libs.kotlin.test)
                api(libs.kotlin.test.junit5)
                api(libs.junit.jupiter)
            }
        }
    }
}

android {
    namespace  = "${Artifact.APP_ID}"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
}

compose.resources {
    publicResClass    = true
    packageOfResClass = "${Artifact.APP_ID}.res"
    generateResClass  = always
}
