plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.umkmbuhar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.umkmbuhar"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
//    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
//    implementation("com.google.firebase:firebase-database")
//    implementation("com.google.firebase:firebase-storage")
//    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3") // Untuk HTTP Client
    implementation ("com.google.code.gson:gson:2.9.0")  // Untuk JSON Parsing

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
//    implementation ("io.supabase:supabase-android:0.0.1")
//    implementation ("io.supabase:postgrest-android:0.0.1")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
