plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.leone.okoleo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.leone.okoleo"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)

    // Jetpack Compose and AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Kotlin Stdlib
    implementation(libs.kotlin.stdlib)

    // ZXing barcode scanner
    implementation(libs.zxing)

    // Accompanist permissions
    implementation(libs.accompanist.permissions)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.androidx.navigation.compose)
    implementation (libs.androidx.ui.v150)
    implementation (libs.androidx.material)
    implementation (libs.androidx.ui.tooling.preview.v150)
    implementation (libs.androidx.lifecycle.runtime.ktx.v260)
    implementation (libs.androidx.activity.compose.v170)
    implementation (libs.androidx.activity.compose.v180) 
    implementation (libs.androidx.core.ktx.v1120)
    implementation (libs.androidx.material3.v120)



}