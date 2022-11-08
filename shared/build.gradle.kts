plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
}

kotlin {
    android()
    val disableKonanVerification = "-Xverify-compiler=false"

    iosX64("uikitX64") {
        binaries {
            executable() {
                entryPoint = "main"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                    disableKonanVerification
                )
            }
        }
    }
    iosArm64("uikitArm64") {
        binaries {
            executable() {
                entryPoint = "main"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                    disableKonanVerification
                )
            }
        }
    }
    //iosX64()
    //iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                api("io.ktor:ktor-client-core:2.1.2")
                api("io.ktor:ktor-client-cio:2.1.2")
                api("io.ktor:ktor-client-content-negotiation:2.1.2")
                api("io.ktor:ktor-serialization-kotlinx-json:2.1.2")
                api("io.ktor:ktor-client-logging:2.1.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        //val iosX64Main by getting
        //val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            //iosX64Main.dependsOn(this)
            //iosArm64Main.dependsOn(this)

            iosSimulatorArm64Main.dependsOn(this)
        }
        //val iosX64Test by getting
        //val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            //iosX64Test.dependsOn(this)
            //iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
        val uikitMain by creating {
            dependsOn(iosMain)
        }
        val uikitX64Main by getting {
            dependsOn(uikitMain)
        }
        val uikitArm64Main by getting {
            dependsOn(uikitMain)
        }
    }
}

android {
    namespace = "com.programmersbox.kmmtest"
    compileSdk = 32
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }
}

compose.experimental {
    web.application {}
    uikit.application {
        bundleIdPrefix = "org.jetbrains"
        projectName = "KMMTest"
        deployConfigurations {
            simulator("IPhone8") {
                //Usage: ./gradlew iosDeployIPhone8Debug
                device = org.jetbrains.compose.experimental.dsl.IOSDevices.IPHONE_8
            }
            simulator("IPad") {
                //Usage: ./gradlew iosDeployIPadDebug
                device = org.jetbrains.compose.experimental.dsl.IOSDevices.IPAD_MINI_6th_Gen
            }
        }
    }
}
