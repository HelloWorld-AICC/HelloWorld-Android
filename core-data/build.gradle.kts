plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)

    id("kotlin-kapt")                     // kapt 플러그인 추가
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.core.data"
    compileSdk = 34   // 36 대신 34 권장 (안정화된 최신 LTS)

    defaultConfig {
        minSdk = 24   // 보통 23~24 기준으로
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17   // 최신 JDK와 맞춤
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Module Inject
    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)     // ksp → kapt 변경

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // AndroidX & Test
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
