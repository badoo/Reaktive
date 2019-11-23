object Deps {

    private const val kotlinVersion = "1.3.60"
    private const val coroutinesVersion = "1.3.2-1.3.60"
    private const val detektVersion = "1.1.1"
    private const val asmVersion = "6.0"

    val kotlin = Kotlin
    val kotlinx = Kotlinx
    val android = Android
    val jmh = Jmh
    val detekt = Detekt
    val asm = Asm()
    val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.7"
    val rxjava3 = "io.reactivex.rxjava3:rxjava:3.0.0-RC3"
    val picasso = "com.squareup.picasso:picasso:2.71828"
    val bintray = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4"
    val shadow = "com.github.jengelman.gradle.plugins:shadow:5.1.0"

    object Kotlin {
        val stdlib = Stdlib()
        val test = Test
        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"

        class Stdlib(
            private val name: String = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
        ) : CharSequence by name {
            val jdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion"
            val js = "org.jetbrains.kotlin:kotlin-stdlib-js:$kotlinVersion"
            val common = "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion"

            override fun toString(): String = name
        }

        object Test {
            const val common = "org.jetbrains.kotlin:kotlin-test-common:$kotlinVersion"
            const val js = "org.jetbrains.kotlin:kotlin-test-js:$kotlinVersion"
            const val junit = "org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion"
            const val annotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:$kotlinVersion"
        }
    }

    object Kotlinx {
        val coroutines = Coroutines
        val metadata = Metadata

        object Coroutines {
            val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion"
            val core = Core()

            class Core(
                private val name: String = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
            ) : CharSequence by name {
                val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$coroutinesVersion"
                val native = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:$coroutinesVersion"
                val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$coroutinesVersion"

                override fun toString() = name
            }
        }

        object Metadata {
            const val jvm = "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.1.0"
        }
    }

    object Android {
        const val plugin = "com.android.tools.build:gradle:3.4.1"
        val support = Support

        object Support {
            const val appcompatV7 = "com.android.support:appcompat-v7:28.0.0"
            const val exifinterface = "com.android.support:exifinterface:28.0.0"
            const val constraintLayout = "com.android.support.constraint:constraint-layout:1.1.3"
        }
    }

    object Jmh {
        const val plugin = "me.champeau.gradle:jmh-gradle-plugin:0.5.0-rc-2"
        const val core = "org.openjdk.jmh:jmh-core:1.21"
    }

    object Detekt {
        const val plugin = "io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektVersion"
        const val ktlint = "io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion"
    }

    class Asm(
        private val name: String = "org.ow2.asm:asm:$asmVersion"
    ) : CharSequence by name {
        val tree = "org.ow2.asm:asm-tree:$asmVersion"

        override fun toString() = name
    }
}
