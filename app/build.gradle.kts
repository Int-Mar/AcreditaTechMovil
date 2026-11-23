plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.hygeonix.AcreditaTech"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.hygeonix.AcreditaTech"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Dirección del servidor (Render). Se cambia en gradle.properties (API_BASE_URL),
        // no en el código. Para el emulador con el servidor local: http://10.0.2.2:4000/
        val apiBaseUrl = (project.findProperty("API_BASE_URL") as String?)
            ?: "https://acreditatech.onrender.com/"
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Red: Retrofit + Gson para hablar con el servidor en Render
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}