// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.13.2" apply false
    id("com.android.library") version "8.13.2" apply false

    // SUDAH DIPERBAIKI: Mengubah 1.8.0 menjadi 1.8.10 agar sesuai dengan Compose Compiler 1.4.3
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false

    // Plugin Google Services untuk Firebase
    id("com.google.gms.google-services") version "4.4.1" apply false
}