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
    maven("https://dl.bintray.com/subroh0508/maven")
}

dependencies {
    val kotlinWrapperVersion = "pre.93-kotlin-1.3.70"
    val reactVersion = "16.13.0"
    val reactRouterVersion = "4.3.1"
    val reduxVersion = "4.0.0"
    val reactReduxVersion = "5.0.7"
    val ktorVersion = "1.3.1"
    val serializationVersion = "0.20.0"

    implementation(kotlin("stdlib-js"))
    implementation(project(":common"))

    // React, React DOM, React Router DOM + Wrappers
    implementation("org.jetbrains:kotlin-react:$reactVersion-$kotlinWrapperVersion")
    implementation("org.jetbrains:kotlin-react-dom:$reactVersion-$kotlinWrapperVersion")
    implementation("org.jetbrains:kotlin-react-router-dom:$reactRouterVersion-$kotlinWrapperVersion")
    implementation("org.jetbrains:kotlin-redux:$reduxVersion-$kotlinWrapperVersion")
    implementation("org.jetbrains:kotlin-react-redux:$reactReduxVersion-$kotlinWrapperVersion")
    implementation(npm("react", reactVersion))
    implementation(npm("react-dom", reactVersion))
    implementation(npm("react-router-dom", reactRouterVersion))
    implementation(npm("redux", reduxVersion))
    implementation(npm("react-redux", reactReduxVersion))

    // Kotlin Styled
    implementation("org.jetbrains:kotlin-styled:1.0.0-$kotlinWrapperVersion")
    implementation(npm("styled-components", "^4.4.1"))
    implementation(npm("inline-style-prefixer", "^5.1.2"))

    // Material UI
    implementation("subroh0508.net.kotlinmaterialui:core:0.3.0")
    implementation(npm("@material-ui/core", "^4.8.3"))
    implementation(npm("@material-ui/icons", "^4.5.1"))
    implementation(npm("@material-ui/lab", "^4.0.0-alpha.45"))

    // Coroutines
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
            runTask {
                outputFileName = "static/app.js"
                devServer = devServer?.copy(
                    open = false,
                    proxy = mapOf<String, Any>(
                        "/api" to "http://localhost:8000",
                        "/static" to { null },
                        "/" to "http://localhost:8000"
                    )
                )
            }
            webpackTask {
                outputFileName = "static/app.js"
            }
        }
    }
}
