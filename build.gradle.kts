import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    application //to run JVM part
}

group = "me.agaman.kotlinfullstack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinVersion = "1.6.10"
val serializationVersion = "1.3.0"
val ktorVersion = "1.6.7"
val logbackVersion = "1.2.3"
val reactVersion = "17.0.2"
val reactRouterVersion = "6.2.1"
val reduxVersion = "4.1.2"
val reactReduxVersion = "7.2.6"
val muiVersion = "5.4.1"
val kotlinWrappersVersion = "pre.298-kotlin-$kotlinVersion"

kotlin {
    jvm {
        withJava()
    }
    js {
        browser {
            binaries.executable()
            runTask {
                outputFileName = "static/app.js"
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8001,
                    open = false,
                    proxy = mutableMapOf(
                        "/api" to "http://localhost:8000",
                        "/static" to { null },
                        "/" to "http://localhost:8000"
                    ),
                )
            }
            webpackTask {
                outputFileName = "static/app.js"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-core:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-html-builder:$ktorVersion")
                implementation("io.ktor:ktor-auth:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-json:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:$reactRouterVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:$reduxVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:$reactReduxVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui:$muiVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons:$muiVersion-$kotlinWrappersVersion")

                // Needed for MUI
                implementation(npm("@emotion/react", "11.7.1"))
                implementation(npm("@emotion/styled", "11.6.0"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(webpackTask.destinationDirectory) // bring output files along into the JAR
}


tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

// only necessary until https://youtrack.jetbrains.com/issue/KT-37964 is resolved
distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}
