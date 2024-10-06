import com.android.utils.TraceUtils.simpleId

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true

    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.cronet.embedded)
    implementation(libs.androidx.work.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //screen pixels
    implementation(libs.sdp.android)
    implementation(libs.ssp)
    //koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.android.navigation)
    //retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    //rx
    implementation(libs.rxjava)
    implementation(libs.rxjava2adapter)
    // Zxing barcode dependency
    implementation(libs.zxing)
    implementation(libs.gson)
    // Apache POI for working with Excel files
    implementation(libs.apache.poi)
    implementation(libs.apache.poi.ooxml)
    implementation(libs.commons.compress)
    implementation(libs.poi.android)

    //progress
    // Required for handling compressed XML (.xlsx files)


}