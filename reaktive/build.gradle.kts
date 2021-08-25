plugins {
    id ("mpp-configuration")
    id ("publish-configuration")
    id ("detekt-configuration")
    id("dev.petuska.npm.publish")
}

configuration {
    enableLinuxArm32Hfp()
}

kotlin {
    targets {
//            fromPreset(presets.jvm, "jvmCommon")
//            fromPreset(presets.jvm, "jvmJsCommon")
//            fromPreset(presets.linuxX64, "jvmNativeCommon")
//            fromPreset(presets.linuxX64, "nativeCommon")
//            fromPreset(presets.linuxArm32Hfp, "nativeCommon")
//            fromPreset(presets.linuxX64, "linuxCommon")

//            fromPreset(presets.iosX64, "darwinCommon")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation (project(":utils"))
                implementation (project(":reaktive-annotations"))
            }
        }

        commonTest {
            dependencies {
                implementation( project(":reaktive-testing"))
            }
        }

        all {
            languageSettings.useExperimentalAnnotation("kotlin.js.JsExport")
        }
    }
}

npmPublishing {
    repositories {
        repository("Local") {
            registry = uri("http://localhost:4873")
            authToken = "y7tJdftrqx+2JEsk+I2Q2FW8tG++LFO7wG0YLsjHTgg="
        }
    }

    organization = "com.badoo"
    // access = PUBLIC
}
