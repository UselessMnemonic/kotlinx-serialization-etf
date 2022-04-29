plugins {
    kotlin("multiplatform") version "1.6.20"
    kotlin("plugin.serialization") version "1.6.20"
}

group = "uselessmnemonic.kotlinx"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets {
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = "1.8"
            }
            withJava()
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
        }
        js(BOTH) {
            browser {
                commonWebpackConfig {
                    cssSupport.enabled = true
                }
            }
        }

        val hostOs = System.getProperty("os.name")
        val isMingwX64 = hostOs.startsWith("Windows")
        val nativeTarget = when {
            hostOs == "Mac OS X" -> macosX64("native")
            hostOs == "Linux" -> linuxX64("native")
            isMingwX64 -> mingwX64("native")
            else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
        }

        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.20")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting
        val jsMain by getting
        val nativeMain by getting
    }
}
