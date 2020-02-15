plugins {
    kotlin("js") version "1.3.61"
}

group = "me.agaman.kotlinfullstack"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
    jcenter()
}

kotlin {
    target {
        browser {
            webpackTask {
                outputFileName = "static/app.js"
            }
        }
    }
    sourceSets {
        main {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(project(":common"))

                //React, React DOM, React Router DOM + Wrappers
                implementation("org.jetbrains:kotlin-react:16.9.0-pre.91-kotlin-1.3.61")
                implementation("org.jetbrains:kotlin-react-dom:16.9.0-pre.91-kotlin-1.3.61")
                implementation("org.jetbrains:kotlin-react-router-dom:4.3.1-pre.91-kotlin-1.3.61")
                implementation(npm("react", "16.12.0"))
                implementation(npm("react-dom", "16.12.0"))
                implementation(npm("react-router-dom", "4.3.1"))

                //Kotlin Styled
                implementation("org.jetbrains:kotlin-styled:1.0.0-pre.90-kotlin-1.3.61")
                implementation(npm("styled-components"))
                implementation(npm("inline-style-prefixer"))

                //Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.3")
            }
        }
    }
}
