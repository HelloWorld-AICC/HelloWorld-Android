plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.feature"
    compileSdk = 36

    defaultConfig {
        minSdk = 32

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            type = "String",
            name = "APP_VERSION_NAME",
            value = "\"${rootProject.extra["versionName"] ?: "1.0.0"}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                type = "String",
                name = "APP_VERSION_NAME",
                value = "\"${rootProject.extra["versionName"] ?: "1.0.0"}\"",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Module Inject
    implementation(project(":core:model"))
    implementation(project(":core-data"))
    implementation(project(":core-ui"))
    implementation(project(":core:domain"))

    // Lottie for Jetpack Compose 추가
    implementation("com.airbnb.android:lottie-compose:6.1.0")


    // Core Android dependencies
    implementation(libs.androidx.activity.compose)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.material.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.play.services.auth)
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.installations.interop)
    implementation(libs.firebase.installations)

    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Instrumented tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Local tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")


    //animation
    implementation("androidx.compose.animation:animation-core:1.6.1") // Compose 버전에 따라 조정

    // Gson
    implementation("com.google.code.gson:gson:2.11.0")

    // oss-licenses
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.oss.licenses)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.video)

    // Exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.ui.compose)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}