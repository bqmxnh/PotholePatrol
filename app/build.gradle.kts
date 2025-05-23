plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.potholepatrol"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.potholepatrol"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.core.splashscreen)
    implementation("com.google.android.gms:play-services-auth:21.2.0")
// Glide dependencies
    implementation(libs.glide.core)
    implementation(libs.credentials)
    implementation(libs.googleid)
    implementation(libs.play.services.maps)
    annotationProcessor(libs.glide.compiler)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.0")
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.work:work-runtime:2.8.1")



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}