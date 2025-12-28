plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.android.cineverse"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.android.cineverse"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            resValue("string", "app_name", "CineVerse (Debug)")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "CineVerse")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    // Allow debug and release to be installed side by side
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            output.outputFileName = "CineVerse-${variant.buildType.name}-v${variant.versionName}.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.compose.material.icons.core)

    // Coil
    implementation(libs.coil.compose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    
    // Paging 3
    implementation("androidx.paging:paging-runtime-ktx:3.3.0")
    implementation("androidx.paging:paging-compose:3.3.0")
    
    // Decompose - Component-based navigation
    implementation("com.arkivanov.decompose:decompose:2.2.2")
    implementation("com.arkivanov.decompose:extensions-compose-jetpack:2.2.2")

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.testJunit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("app.cash.turbine:turbine:1.0.0")

    implementation("org.android.cineverse:shared:1.0.0")
}

