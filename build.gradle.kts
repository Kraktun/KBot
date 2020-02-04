import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.3.61"
    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
}

group = "com.kraktun"
version = "0.5.0"

val coroutinesVersion = "1.3.3"
val kotlinVersion = "1.3.61"
val quartzVersion = "2.3.2"
val telegramVersion = "4.6"
val kUtilsVersion = "bde9d66"

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
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("junit:junit:4.12")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.telegram:telegrambots:$telegramVersion")
    implementation("org.telegram:telegrambots-meta:$telegramVersion")
    implementation("org.telegram:telegrambotsextensions:$telegramVersion")
    implementation("org.quartz-scheduler:quartz:$quartzVersion")
    implementation("org.slf4j:slf4j-log4j12:1.7.26")
    implementation("com.github.Kraktun:KUtils:$kUtilsVersion")
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    disabledRules.set(setOf("no-wildcard-imports"))
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = project.name
    manifest {
        attributes["Implementation-Title"] = "KBot"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "com.kraktun.kbot.MainKt"
    }
    from(configurations.runtime.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
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
