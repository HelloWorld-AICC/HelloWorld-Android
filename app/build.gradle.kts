import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
}

val googleMapKey: String = if (project.hasProperty("google_map_key")) {
    (project.property("google_map_key") as String).replace("\"", "")
} else {
    ""
}

android {
    namespace = "com.example.helloworld"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.helloworld"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🔐 구글 맵 키만 리소스로 노출
        resValue("string", "google_map_key", googleMapKey)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        implementation("com.airbnb.android:lottie-compose:6.1.0")
        implementation(project(":core-ui"))
        implementation(project(":feature"))
        implementation(libs.androidx.navigation.compose)

        val composeBom = platform("androidx.compose:compose-bom:2025.02.00")
        implementation(composeBom)
        androidTestImplementation(composeBom)

        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.material:material")
        implementation("androidx.compose.foundation:foundation")
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-tooling-preview")
        debugImplementation("androidx.compose.ui:ui-tooling")
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-test-manifest")
        implementation("androidx.compose.material:material-icons-core")
        implementation("androidx.compose.material:material-icons-extended")
        implementation("androidx.compose.material3.adaptive:adaptive")
        implementation("androidx.activity:activity-compose:1.10.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
        implementation("androidx.compose.runtime:runtime-livedata")
        implementation("androidx.compose.runtime:runtime-rxjava2")

        implementation(libs.hilt.android)
        ksp(libs.hilt.android.compiler)

        implementation(libs.androidx.appcompat)
        implementation(libs.play.services.oss.licenses)

        implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation(libs.firebase.auth)
        implementation(libs.play.services.auth)

        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

        implementation(project(":core-data"))
    }
}
