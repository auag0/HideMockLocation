plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.auag0.hidemocklocation"
    compileSdk = 34
    defaultConfig {
        applicationId = "io.github.auag0.hidemocklocation"
        minSdk = 23
        targetSdk = 34
        versionCode = 100000
        versionName = "1.0.0"
    }
    signingConfigs {
        create("release") {
            storeFile = File(projectDir, "release-keystore.jks")
            storePassword = System.getenv("storePassword")
            keyAlias = System.getenv("keyAlias")
            keyPassword = System.getenv("keyPassword")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures.buildConfig = true
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.android.support.v4)

    compileOnly(libs.xposed.api)
    compileOnly(libs.xposed.api.sources)
}