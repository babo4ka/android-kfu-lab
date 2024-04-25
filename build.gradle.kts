// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

//ext {
//    set("room_version", "2.2.1")
//    roomVersion = "2.2.1"
//    archLifecycleVersion = "2.2.0-rc02"
//    androidxArchVersion = "2.1.0"
//    coreTestingVersion = "2.1.0"
//    coroutines = "1.3.2"
//    materialVersion = "1.0.0"
//}