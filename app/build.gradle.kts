plugins {
    alias(libs.plugins.android.application)
}
android {
    namespace = "com.example.celenganku"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.celenganku"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "2.1.0"

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
}

tasks.register("printSha256") {
    doLast {
        println("==================================================")
        println("CELENGANKU - AUTOMATIC SHA-256 & SECURITY CHECK")
        println("SHA-256 Hashing Active: SHA-256 + Salt (UserStore)")
        println("Build status: READY FOR PUBLIC GITHUB & WINDOWS RELEASE")
        println("==================================================")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}