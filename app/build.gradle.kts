import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.android.gms.oss-licenses-plugin")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        load(file.inputStream())
    }
}

val googleMapKey = localProperties.getProperty("google_map_key") ?: ""

android {
    namespace = "com.example.hello_world_mvp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.hello_world_mvp"
        minSdk = 32
        targetSdk = 36
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

        // Module Inject
        implementation(project(":core:model"))
        implementation(project(":core:network"))
        implementation(project(":core-data"))
        implementation(project(":core-ui"))
        implementation(project(":feature"))

        implementation("com.airbnb.android:lottie-compose:6.1.0")
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

        implementation(libs.play.services.auth)

        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    }
}