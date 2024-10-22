import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "bd.edu.daffodilvarsity.classorganizer"
    compileSdk = 34

    defaultConfig {
        applicationId = "bd.edu.daffodilvarsity.classorganizer"
        minSdk = 21
        targetSdk = 34
        versionCode = 85
        versionName = "5.1.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }

    dataBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    // Material intro (Use compiled lib until a new release with fixes)
    api(project(":material_intro"))
    implementation("com.google.firebase:firebase-crashlytics:19.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0-alpha4") {
        exclude(group = "com.android.support", module = "support-annotations")
    }

    // All support lib
    val supportLibVersion = "1.0.0"
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.legacy:legacy-support-v13:$supportLibVersion")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.cardview:cardview:$supportLibVersion")
    implementation("androidx.legacy:legacy-support-v4:$supportLibVersion")
    implementation("androidx.vectordrawable:vectordrawable:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.asynclayoutinflater:asynclayoutinflater:$supportLibVersion")
    implementation("androidx.core:core:1.13.1")

    // Material dialogues
    implementation("com.afollestad.material-dialogs:core:0.9.6.0")

    // GSON for saving objects
    implementation("com.google.code.gson:gson:2.10.1")

    // Firebase core and messaging
    implementation("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-messaging:24.0.3")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")

    // RX JAVA
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("io.reactivex.rxjava2:rxjava:2.2.9")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")

    // Toasty
    implementation("com.github.GrenderG:Toasty:1.3.0")

    // Butterknife
    implementation("com.jakewharton:butterknife:10.1.0")
    annotationProcessor("com.jakewharton:butterknife-compiler:10.1.0")

    // BRVAH
    implementation("com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50")

    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-rxjava2:$roomVersion")

    // Custom tabs
    implementation("androidx.browser:browser:1.8.0")

    val lifecycleVersion = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.8.6")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.8.6")
    implementation("androidx.lifecycle:lifecycle-reactivestreams:2.8.6")

    val lifecycleArchVersion = "1.1.1"
    implementation("android.arch.lifecycle:extensions:$lifecycleArchVersion")
    implementation("android.arch.lifecycle:common-java8:$lifecycleArchVersion")
    implementation("android.arch.lifecycle:reactivestreams:$lifecycleArchVersion")

    val workVersion = "2.9.1"
    implementation("androidx.work:work-runtime:$workVersion")
    implementation("androidx.work:work-rxjava2:$workVersion")
}
