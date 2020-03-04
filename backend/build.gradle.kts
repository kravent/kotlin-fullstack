group = "me.agaman.kotlinfullstack"
version = "1.0-SNAPSHOT"

plugins {
    application
    kotlin("jvm") version "1.3.61"
    kotlin("plugin.serialization") version "1.3.61"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val ktorVersion = "1.3.1"
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation(project(":common"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    processResources {
        dependsOn(":frontend:browserWebpack")
    }
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test/kotlin")

sourceSets["main"].resources.srcDirs("src/main/resources", "../frontend/build/distributions")
sourceSets["test"].resources.srcDirs("src/test/resources")

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}
