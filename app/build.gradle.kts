import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

// Datos para firmar el APK.
// Las contraseñas están en keystore.properties y no se suben a GitHub.
val archivoFirma = rootProject.file("keystore.properties")
val datosFirma = Properties()

if (archivoFirma.exists()) {
    archivoFirma.inputStream().use {
        datosFirma.load(it)
    }
}

android {
    namespace = "com.duoc.kaijuinventory"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.duoc.kaijuinventory"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (archivoFirma.exists()) {
                storeFile = rootProject.file(
                    datosFirma.getProperty("storeFile")
                )
                storePassword = datosFirma.getProperty("storePassword")
                keyAlias = datosFirma.getProperty("keyAlias")
                keyPassword = datosFirma.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
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

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.9.0")
    implementation("androidx.compose.material:material-icons-core")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // DataStore: se mantiene para guardar la sesión del usuario.
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room: persistencia local de productos y movimientos.
    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.room:room-ktx:2.7.2")
    ksp("androidx.room:room-compiler:2.7.2")

    // Retrofit: microservicios y API externa.
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Pruebas unitarias.
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Pruebas instrumentadas y de interfaz.
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
