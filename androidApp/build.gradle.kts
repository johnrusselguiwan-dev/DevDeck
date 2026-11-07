import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
}

val versionProperties = Properties().apply {
    rootProject.file("version.properties").inputStream().use { load(it) }
}
val appVersionCode = versionProperties.getProperty("VERSION_CODE")?.toIntOrNull()
    ?.takeIf { it > 0 } ?: error("VERSION_CODE must be a positive integer in version.properties")
val appVersionName = versionProperties.getProperty("VERSION_NAME")
    ?.takeIf { it.matches(Regex("[A-Za-z0-9][A-Za-z0-9._-]*")) }
    ?: error("VERSION_NAME must be filename-safe in version.properties")
val requireReleaseSigning = providers.gradleProperty("requireReleaseSigning").map(String::toBoolean).orElse(false)

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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
    androidTestImplementation(libs.androidx.testExt.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.compose.uiTestJunit4)
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
        htmlReport = true
        xmlReport = true
        sarifReport = true
    }

    defaultConfig {
        applicationId = "com.example.devdeck"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }
        create("prod") {
            dimension = "env"
        }
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
            isMinifyEnabled = true
            isShrinkResources = true
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

val validateReleaseSigning by tasks.registering {
    val configured = releaseSigningFile.isFile
    doLast {
        check(configured) { "Signed release packaging requires secret.properties" }
    }
}

if (requireReleaseSigning.get()) {
    tasks.matching { it.name in setOf("assembleProdRelease", "bundleProdRelease", "packageProdRelease") }
        .configureEach { dependsOn(validateReleaseSigning) }
}
