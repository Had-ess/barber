plugins {
    alias(libs.plugins.android.application)
    // Add if using Kotlin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.youssef.barber"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.youssef.barber"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17 // Updated from 1.8
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // AndroidX Libraries (using version catalog)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Firebase BoM (Bill of Materials) - manages versions for you
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")

    // UI Components
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // Image Loading
    implementation("com.squareup.picasso:picasso:2.8") // Updated version

    // Date/Time
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.6")

    // Notifications
    implementation("androidx.core:core-ktx:1.12.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}