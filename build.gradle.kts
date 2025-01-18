import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    kotlin("jvm") version "2.0.20"
}

group = "ru.ntcrckr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("com.github.sya-ri:kgit:1.1.0")
}

tasks.test {
    useJUnitPlatform()
}