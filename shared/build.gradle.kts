import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

@OptIn(ExperimentalComposeLibrary::class)
kotlin {
    androidTarget()
    applyDefaultHierarchyTemplate {
        common {
            group("mobile") {
                withAndroidTarget()
                withIos()
            }
            group("uikit") {
                withIosX64()
                withIosArm64()
                withIosSimulatorArm64()
            }
        }
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvm.get()))
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
                api(kotlin("stdlib-jdk8"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material3)
                api(compose.ui)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.components.resources)
                api(compose.components.uiToolingPreview)
            }
        }
        val commonTest by getting {
            dependencies {
		api(kotlin("test-junit5"))
                api(libs.junit.jupiter)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.uiTest)
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes") 
    }
}

android {
    namespace  = project.findProperty("APP_ID")
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.min.sdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}

compose.resources {
    publicResClass    = true
    packageOfResClass = "${project.findProperty("APP_ID")}.res"
    generateResClass  = always
}
