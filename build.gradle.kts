import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    application //to run JVM part
}

group = "me.agaman.kotlinfullstack"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val serializationVersion = "1.3.0"
val ktorVersion = "2.0.1"
val logbackVersion = "1.2.3"
val reactVersion = "18.1.0"
val reactRouterVersion = "6.3.0"
val reduxVersion = "4.1.2"
val reactReduxVersion = "7.2.6"
val muiVersion = "5.6.3"
val muiIconsVersion = "5.6.2"
val kotlinWrappersVersion = "pre.333"

kotlin {
    jvm {
        withJava()
    }
    js {
        binaries.executable()
        browser {
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
                implementation("io.ktor:ktor-resources:$ktorVersion")
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
                implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
                implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
                implementation("io.ktor:ktor-server-compression:$ktorVersion")
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                implementation("io.ktor:ktor-server-resources:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
                implementation("io.ktor:ktor-server-auth:$ktorVersion")
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

                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-resources:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:$reactRouterVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-redux:$reduxVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-redux:$reactReduxVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui:$muiVersion-$kotlinWrappersVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons:$muiIconsVersion-$kotlinWrappersVersion")

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
