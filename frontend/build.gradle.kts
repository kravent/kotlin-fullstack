import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("js") version "1.3.70"
    kotlin("plugin.serialization") version "1.3.70"
}

group = "me.agaman.kotlinfullstack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
}

dependencies {
    val ktorVersion = "1.3.1"
    val serializationVersion = "0.20.0"

    implementation(kotlin("stdlib-js"))
    implementation(project(":common"))

    //React, React DOM, React Router DOM + Wrappers
    implementation("org.jetbrains:kotlin-react:16.9.0-pre.91-kotlin-1.3.61")
    implementation("org.jetbrains:kotlin-react-dom:16.9.0-pre.91-kotlin-1.3.61")
    implementation("org.jetbrains:kotlin-react-router-dom:4.3.1-pre.91-kotlin-1.3.61")
    implementation("org.jetbrains:kotlin-redux:4.0.0-pre.91-kotlin-1.3.61")
    implementation("org.jetbrains:kotlin-react-redux:5.0.7-pre.91-kotlin-1.3.61")
    implementation(npm("react", "16.12.0"))
    implementation(npm("react-dom", "16.12.0"))
    implementation(npm("react-router-dom", "4.3.1"))
    implementation(npm("redux", "4.0.0"))
    implementation(npm("react-redux", "5.0.7"))

    //Kotlin Styled
    implementation("org.jetbrains:kotlin-styled:1.0.0-pre.90-kotlin-1.3.61")
    implementation(npm("styled-components"))
    implementation(npm("inline-style-prefixer"))

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3")

    // Ajax calls
    implementation("io.ktor:ktor-client-js:$ktorVersion")
    implementation("io.ktor:ktor-client-json-js:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-js:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$serializationVersion")
    implementation(npm("text-encoding"))
    implementation(npm("abort-controller"))

    implementation(npm("fs"))
    implementation(npm("bufferutil"))
    implementation(npm("utf-8-validate"))
}

kotlin {
    target {
        useCommonJs()
        browser {
            webpackTask {
                outputFileName = "static/app.js"
            }
        }
    }
}
