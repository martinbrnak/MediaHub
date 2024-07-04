plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("androidx.navigation.safeargs.kotlin")
}



android {
    namespace = "com.MartinBrnak.mediahub"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.MartinBrnak.mediahub"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources.excludes.add("META-INF/*")
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
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures{
        viewBinding = true

    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.moshi:moshi:1.13.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("io.coil-kt:coil:2.0.0-rc02")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("com.google.cloud:google-cloud-video-intelligence:2.41.0")
    implementation("io.grpc:grpc-okhttp:1.62.2")
    implementation("io.grpc:grpc-core:1.62.2")
    implementation("io.grpc:grpc-stub:1.62.2")
    testImplementation("junit:junit:4.13.2")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.6.8")
    implementation("androidx.compose.runtime:runtime:1.6.8")
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.ui:ui-tooling:1.6.8")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    kapt("androidx.room:room-compiler:2.6.1")
    kapt("com.github.bumptech.glide:compiler:4.12.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

}