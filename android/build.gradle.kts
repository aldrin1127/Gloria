import com.android.build.gradle.api.BaseVariant
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
}

kotlin {
    sourceSets {
        val androidMain by getting {
            dependencies {
                api(projects.shared)
                api(libs.androidx.activity.ktx)
                api(libs.androidx.activity.compose)
            }
        }
        val androidTest by getting {
            dependencies {
		api(projects.shared)
		api(libs.junit.jupiter.params)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.uiTest)
            }
        }
        val androidDebug by getting {
            dependencies {
                api(libs.leakcanary.android)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.uiTooling)
            }
        }
    }
}

android {
    namespace  = "${project.findProperty("APP_ID")}.android"
    compileSdk = libs.versions.android.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = "${project.findProperty("APP_ID")}.android"
        minSdk        = libs.versions.android.min.sdk.get().toInt()
        targetSdk     = libs.versions.android.target.sdk.get().toInt()
        versionCode   = project.findProperty("VERSION_CODE") as Int
	versionName   = project.findProperty("VERSION_NAME") as String
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled   = true
            isShrinkResources = true
            signingConfig     = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
    buildFeatures {
        compose     = true
	dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.get()
    }
}
