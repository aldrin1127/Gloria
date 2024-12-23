plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.leakcanary.deobfuscation)
}

kotlin {
    jvmToolchain(libs.versions.jvm.get().toDouble())
    androidTarget()

    sourceSets {
        val androidMain by getting {
            dependencies {
                api(projects.shared)
                api(libs.androidx.activity.ktx)
                api(libs.androidx.activity.compose)
            }
        }
        val androidDebug by getting {
            dependencies {
                api(compose.uiTooling)
                api(libs.leakcanary.android)
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
        versionCode   = project.findProperty("VERSION_CODE").toInt()
	versionName   = project.findProperty("VERSION_NAME")
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/res")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvm.get())
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.get()
    }
    dataBinding {
        enabled = true
    }
}

leakCanary {
    filterObfuscatedVariants {
        it.name == "debug"
    }
}
