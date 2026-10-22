import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

compose.resources {
    packageOfResClass = "com.example.devdeck.shared.generated.resources"
}

val generatePortfolioOverview by tasks.registering(Exec::class) {
    val generator = rootProject.file("scripts/generate-portfolio-overview.mjs")
    val content = file("src/commonMain/composeResources/files/portfolio.json")
    val output = layout.buildDirectory.dir("generated/portfolioOverview")
    inputs.files(generator, content)
    outputs.dir(output)
    commandLine("node", generator.absolutePath, output.get().file("overview.html").asFile.absolutePath)
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
            testTask {
                useKarma {
                    useChromeHeadless()
                }
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
        wasmJsMain {
            resources.srcDir(generatePortfolioOverview)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(compose.components.resources)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.androidx.testExt.junit)
            implementation(libs.androidx.test.runner)
        }
    }
}

dependencies {
    add("debugImplementation", libs.compose.uiTooling)
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
        htmlReport = true
        xmlReport = true
        sarifReport = true
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
