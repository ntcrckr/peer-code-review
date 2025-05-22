plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.compose") version "1.8.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
}

group = "ru.ntcrckr"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // Miscellaneous
    implementation("ch.qos.logback:logback-classic:1.4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.2")

    // KGit + JGit for running git CLI commands
    implementation("com.github.sya-ri:kgit:1.1.0")
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:7.0.0.202409031743-r")
    implementation("com.github.mwiede:jsch:0.2.25")

    // GitHub REST API wrapper
    implementation("com.jcabi:jcabi-github:1.9.1")
    implementation("org.glassfish:javax.json:1.1.4")

    // Exposed core and DAO modules
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    // H2 database driver
    implementation("com.h2database:h2:2.3.232")

    // UI
    implementation(compose.desktop.currentOs)
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}