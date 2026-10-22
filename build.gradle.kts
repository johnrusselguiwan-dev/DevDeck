plugins {
    
    
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.detekt) apply false
}

subprojects {
    plugins.apply("io.gitlab.arturbosch.detekt")
    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        parallel = false
        baseline = rootProject.file("config/detekt/baseline-${project.name}.xml")
        source.setFrom(fileTree("src") {
            include("**/*.kt", "**/*.kts")
            exclude("**/build/**", "**/generated/**")
        })
        basePath = rootProject.projectDir.absolutePath
    }

    dependencies {
        add("detektPlugins", rootProject.libs.detekt.formatting)
    }
}
