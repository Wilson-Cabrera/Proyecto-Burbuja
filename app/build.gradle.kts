plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.wilson.burbuja"
    // Mantengo 36 porque tus librerías lo exigen
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wilson.burbuja"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true // Activado para exponer GEMINI_API_KEY desde local.properties
    }
}

dependencies {
    // Nucleo de AndroidX y Compose (Vía Catálogo)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Navegación e Iconos
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.material:material-icons-extended")

    // CameraX
    val camerax_version = "1.4.0"
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")

    // Coil para imágenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Firebase (Versión de tu consola)
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // SDK de Google AI para Gemini
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.tooling)
}

// --- PARCHE DEFINITIVO PARA COMPATIBILIDAD CON SDK 36 ---
configurations.all {
    resolutionStrategy {
        // Solución al crash de "ProtectionLayout" y "EdgeToEdge"
        force("androidx.core:core-ktx:1.15.0")
        force("androidx.activity:activity-compose:1.9.3")
        force("androidx.activity:activity-ktx:1.9.3")

        // Estabilidad para Lifecycle
        force("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
        force("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
        force("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    }
}