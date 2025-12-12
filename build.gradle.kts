import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "xyz.angames.anguardaig"
version = "3.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly(files("libs/ProtocolLib.jar"))
    implementation("com.google.flatbuffers:flatbuffers-java:23.5.26")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<ShadowJar> {
        archiveClassifier.set("")
        mergeServiceFiles()
        from(sourceSets.main.get().output)
    }
    named<Task>("build") {
        dependsOn(shadowJar)
    }
}