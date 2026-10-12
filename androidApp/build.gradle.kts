import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

val releaseSigningFile = rootProject.file("secret.properties")
val releaseSigningProperties = Properties().apply {
    if (releaseSigningFile.isFile) {
        releaseSigningFile.inputStream().use { load(it) }
    }
}

fun releaseSigningProperty(name: String): String =
    releaseSigningProperties.getProperty(name)?.takeIf { it.isNotBlank() }
        ?: error("Missing signing property '$name' in secret.properties")

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}

android {
    namespace = "com.example.devdeck"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    lint {
        abortOnError = true
        checkDependencies = true
        ignoreTestSources = true
        error += listOf("HardcodedText", "ResourceAsColor")
        disable += listOf("QueryAllPackagesPermission")
    }

    defaultConfig {
        applicationId = "com.example.devdeck"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        if (releaseSigningFile.isFile) {
            create("release") {
                storeFile = rootProject.file(releaseSigningProperty("RELEASE_STORE_FILE"))
                storePassword = releaseSigningProperty("RELEASE_STORE_PASSWORD")
                keyAlias = releaseSigningProperty("RELEASE_KEY_ALIAS")
                keyPassword = releaseSigningProperty("RELEASE_KEY_PASSWORD")
                require(storeFile?.isFile == true) { "Configured release keystore does not exist" }
            }
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
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
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}
