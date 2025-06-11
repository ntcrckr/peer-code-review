plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jetbrains.compose") version "1.8.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.21"
}

group = "ru.ntcrckr"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    testImplementation(kotlin("test"))

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.12")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.7.3")

    // KGit + JGit for Git CLI commands
    implementation("com.github.sya-ri:kgit:1.1.0")
    implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:7.0.0.202409031743-r")
    implementation("com.github.mwiede:jsch:0.2.25")

    // GitHub REST API wrapper
    implementation("com.jcabi:jcabi-github:1.9.1")
    implementation("org.glassfish:javax.json:1.1.4")

    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.43.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")

    // H2 database driver
    implementation("com.h2database:h2:2.3.232")

    // Compose Desktop UI + Material
    implementation(compose.desktop.currentOs)

    // Material Icons Extended
    implementation("org.jetbrains.compose.material3:material3-desktop:1.1.0")
}

tasks.test {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}