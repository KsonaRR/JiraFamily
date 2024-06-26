

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.example.jirafamily"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.example.jirafamily"
        minSdk = 27
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
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("androidx.core:core-ktx:1.7.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.javafaker:javafaker:1.0.2")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation(libs.play.services.fido)
    implementation(libs.androidx.ui.graphics.android)
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.firebase:firebase-storage")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}