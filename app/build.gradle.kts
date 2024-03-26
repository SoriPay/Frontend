

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.graduation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.graduation"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding=true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    //프래그먼트
    implementation("androidx.fragment:fragment-ktx:1.3.6")

    //카드 고를 때 들어갈 인디케이터
    implementation("com.tbuonomo:dotsindicator:5.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.google.android.material:material:1.2.0")

    //지문인식
    implementation ("androidx.biometric:biometric:1.1.0")

    //계좌번호 카메라로 인식
    implementation ("com.rmtheis:tess-two:5.4.1")
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    //서버 통신을 위한 Retrofit 환경 세팅

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
   // implementation ("com.google.code.gson:gson:2.8.6")
   // implementation ("com.squareup.okhttp3:logging-interceptor:3.11.0")

    implementation ("mysql:mysql-connector-java:5.1.46")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")



}