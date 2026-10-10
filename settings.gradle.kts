pluginManagement {
    plugins {
        id("dev.architectury.loom") version "1.17-SNAPSHOT"
        id("dev.architectury.loom-no-remap") version "1.17-SNAPSHOT"
        id("architectury-plugin") version "3.5-SNAPSHOT"
        id("com.gradleup.shadow") version "9.4.3"
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.architectury.dev/") { name = "Architectury" }
        maven("https://maven.minecraftforge.net/") { name = "Forge" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForge" }
        maven("https://maven.kikugie.dev/releases") { name = "Stonecutter" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.7"
}

rootProject.name = "Genshinecraft"

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true

    create(rootProject) {
        versions("1.20.1", "1.21.1")
        versions("26.3").buildscript("build.26.gradle.kts")

        branch("fabric") {
            inherit()
            versions("26.3").buildscript("build.26.gradle.kts")
        }

        branch("neoforge") {
            versions("1.21.1")
            versions("26.3").buildscript("build.26.gradle.kts")
        }

        branch("forge") {
            versions("1.20.1")
        }

        vcsVersion = "1.21.1"
    }
}
