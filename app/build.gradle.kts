plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.storyapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.storyapp"
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


    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
            all {
                it.useJUnitPlatform()
            }
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
        viewBinding = true
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.activity)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)
        implementation(libs.androidx.gridlayout)
        implementation(libs.logging.interceptor)
        implementation(libs.okhttp)
        implementation(libs.glide)
        implementation(libs.retrofit)
        implementation(libs.converter.gson)
        implementation(libs.androidx.paging.runtime.ktx)
        implementation(libs.play.services.maps)


        androidTestImplementation(libs.androidx.core.testing) //InstantTaskExecutorRule
        androidTestImplementation(libs.kotlinx.coroutines.test) //TestDispatcher

        testImplementation(libs.junit)
        testImplementation(libs.androidx.core.testing) // InstantTaskExecutorRule
        testImplementation(libs.kotlinx.coroutines.test) //TestDispatcher
        testImplementation(libs.mockito.core)
        testImplementation(libs.mockito.inline)
        testImplementation("androidx.paging:paging-common:3.2.1")

    }
}
