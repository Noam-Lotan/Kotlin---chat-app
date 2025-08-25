val kotlin_version: String by project
val ktor_version = "2.3.4" // match server's ktor version
val serialization_version = "1.6.0" // match server's kotlinx.serialization version

plugins {
    kotlin("jvm") version "2.1.10" // match server Kotlin version
    kotlin("plugin.serialization") version "2.1.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
    implementation("io.ktor:ktor-client-websockets:${ktor_version}")


    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14") // Logback implementation
}

kotlin {
    jvmToolchain(17)
}
