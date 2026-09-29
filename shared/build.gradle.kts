import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

compose.resources {
    packageOfResClass = "com.example.devdeck.shared.generated.resources"
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.skiko:skiko:0.8.18")
        force("org.jetbrains.skiko:skiko-wasm-js:0.8.18")
        force("org.jetbrains.skiko:skiko-js-wasm-runtime:0.8.18")
        force("org.jetbrains.compose.runtime:runtime:1.7.3")
        force("org.jetbrains.compose.ui:ui:1.7.3")
        force("org.jetbrains.compose.foundation:foundation:1.7.3")
        force("org.jetbrains.compose.material3:material3:1.7.3")
    }
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "devdeck"
        browser {
            commonWebpackConfig {
                outputFileName = "devdeck.js"
            }
        }
        binaries.executable()
    }
    
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin Core & Compose
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.example.devdeck.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    lint {
        abortOnError = true
        checkDependencies = true
        ignoreTestSources = true
        error += listOf("HardcodedText", "ResourceAsColor")
        disable += listOf("QueryAllPackagesPermission")
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}