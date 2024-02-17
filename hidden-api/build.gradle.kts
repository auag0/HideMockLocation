plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.github.auag0.hidden_api"

    compileSdk = 34
}

dependencies {
    annotationProcessor(libs.rikka.refile.annotation.processor)
    compileOnly(libs.rikka.refile.annotation)
}