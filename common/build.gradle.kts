group = "me.agaman.kotlinfullstack"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
}

repositories {
    mavenCentral()
}

kotlin {
    val serializationVersion = "0.14.0"

    jvm {
        compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
            }
        }
    }
    js {
        browser()
        compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
            }
        }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir("src/commonMain/kotlin")
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
            }
        }
        commonTest {
            kotlin.srcDir("src/commonTest/kotlin")
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}
