import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  id(Config.Plugins.androidApplication)
  id(Config.Plugins.kotlinAndroid)
  id(Config.Plugins.kotlinKapt)
  id(Config.Plugins.navigationSafeArgs)
  id(Config.Plugins.hilt)
}

android {
  compileSdk = Config.AppConfig.compileSdkVersion

  defaultConfig {
    applicationId = Config.AppConfig.appId
    minSdk = Config.AppConfig.minSdkVersion
    targetSdk = Config.AppConfig.compileSdkVersion
    versionCode = Config.AppConfig.versionCode
    versionName = Config.AppConfig.versionName

    vectorDrawables.useSupportLibrary = true
    multiDexEnabled = true
    testInstrumentationRunner = Config.AppConfig.testRunner
  }

  buildTypes {
    getByName("debug") {
      resValue("string", "app_name", "Base MVVM-Test")
      resValue("string", "google_api_key", gradleLocalProperties(rootDir).getProperty("GOOGLE_API_KEY"))
      manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_test"
      manifestPlaceholders["appRoundIcon"] = "@mipmap/ic_launcher_test_round"

      buildConfigField("String", "API_BASE_URL", Config.Environments.debugBaseUrl)
    }

    signingConfigs {
      create("releaseConfig") {
        storeFile = file(rootProject.file("key"))
        storePassword = "123456"
        keyAlias = "My-Key"
        keyPassword = "123456"
      }
    }

    getByName("release") {
      signingConfig = signingConfigs.getByName("releaseConfig")

      isMinifyEnabled = true
      isShrinkResources = true

      resValue("string", "app_name", "Base MVVM")
      resValue("string", "google_api_key", gradleLocalProperties(rootDir).getProperty("GOOGLE_API_KEY"))
      manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"
      manifestPlaceholders["appRoundIcon"] = "@mipmap/ic_launcher_round"

      buildConfigField("String", "API_BASE_URL", Config.Environments.releaseBaseUrl)
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  kotlinOptions {
    jvmTarget = "11"
  }

  dataBinding {
    isEnabled = true
  }
}

dependencies {
  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

  // Networking
  implementation(Libraries.retrofit)
  implementation(Libraries.retrofitConverter)
  implementation(Libraries.gson)
  implementation(Libraries.interceptor)
  implementation(Libraries.chuckLogging)

  // Utils
  implementation(Libraries.playServices)
  implementation(Libraries.localization)
  implementation(Libraries.multidex)

  // Hilt
  implementation(Libraries.hilt)
  kapt(Libraries.hiltDaggerCompiler)

  // Project Modules
  implementation(project(Config.Modules.domain))
  implementation(project(Config.Modules.data))
  implementation(project(Config.Modules.presentation))
}

apply {
  from("$rootDir/gradle/install-git-hooks.gradle")
}