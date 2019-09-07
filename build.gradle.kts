import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.3.50"
    id("org.jlleitschuh.gradle.ktlint") version "8.1.0"
}

group = "com.kraktun"
version = "0.4.4"

val coroutinesVersion = "1.3.0-RC2"
val kotlinVersion = "1.3.41"
val sqliteVersion = "3.27.2.1"
val quartzVersion = "2.3.1"
val jsoupVersion = "1.12.1"
val exposedVersion = "0.17.1"
val klaxon = "5.0.11"
val telegramVersion = "4.4.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "${project.group}.kbot.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testCompile("junit:junit:4.12")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    compile("org.telegram:telegrambots:$telegramVersion")
    compile("org.telegram:telegrambots-meta:$telegramVersion")
    compile("org.telegram:telegrambotsextensions:$telegramVersion")
    compile("org.xerial:sqlite-jdbc:$sqliteVersion")
    compile("org.quartz-scheduler:quartz:$quartzVersion")
    compile("org.jsoup:jsoup:$jsoupVersion")
    compile("org.jetbrains.exposed:exposed:$exposedVersion")
    compile("org.slf4j:slf4j-log4j12:1.7.26")
    compile("com.beust:klaxon:$klaxon")
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = project.name
    manifest {
        attributes["Implementation-Title"] = "KBot"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "com.kraktun.kbot.MainKt"
    }
    from(configurations.runtime.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.jvmTarget = "1.8"
}