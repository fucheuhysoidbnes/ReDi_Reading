plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.redi"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.redi"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // 🧩 Fix lỗi trùng META-INF (androidx.versionedparcelable...)
    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/androidx.versionedparcelable_versionedparcelable.version"
            )
        }
    }
}

dependencies {
    // 🖼 Thư viện load ảnh
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.1") {
        exclude(group = "com.android.support") // loại bỏ support v4 cũ
    }

    // 📄 Thư viện đọc PDF (AndroidX version - bản ổn định)
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.1")
    implementation("androidx.collection:collection:1.2.0")

    // ⚙️ Các thư viện AndroidX cơ bản
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // 🔥 Firebase
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)

    // 🧪 Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// 🧩 Fix lỗi Duplicate class android.support.*
configurations.all {
    exclude(group = "com.android.support", module = "support-v4")
    exclude(group = "com.android.support", module = "support-compat")
    exclude(group = "com.android.support", module = "support-core-utils")
}
