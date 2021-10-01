import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.5.31"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.kraktun"
version = "0.6.4"

val coroutinesVersion = "1.5.2-native-mt"
val kotlinVersion = "1.5.31"
val telegramVersion = "5.3.0"
val kUtilsVersion = "441931a"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.telegram:telegrambots:$telegramVersion")
    implementation("org.telegram:telegrambots-meta:$telegramVersion")
    implementation("org.telegram:telegrambotsextensions:$telegramVersion")
    implementation("org.slf4j:slf4j-log4j12:1.7.32")
    implementation("com.github.kraktun:kutils:$kUtilsVersion")
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    disabledRules.set(setOf("no-wildcard-imports"))
}

tasks {
    "build" {
        dependsOn(shadowJar)
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
    kotlinOptions.jvmTarget = "11"
}

val sourcesJar = task("sourcesJar", type = Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

artifacts {
    archives(sourcesJar)
}

java {
    // withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "KBot"
            from(components["java"])
            pom {
                name.set("KBot")
                description.set("Library to write telegram bots in Kotlin")
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}
