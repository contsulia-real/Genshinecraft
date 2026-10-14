plugins {
    id("dev.architectury.loom") apply false
    id("dev.architectury.loom-no-remap")
    id("architectury-plugin")
    id("com.gradleup.shadow") apply false
}

val minecraft = stonecutter.current.version
val modId = project.property("modId").toString()
val modVersion = project.property("modVersion").toString()

version = "$modVersion+$minecraft"
base { archivesName.set("$modId-common") }

architectury.common(listOf("fabric", "neoforge"))

val fabricLoaderVersion = project.property("fabricLoaderVersion").toString()
val architecturyApiVersion = project.property("architecturyApiVersion").toString()

dependencies {
    minecraft("net.minecraft:minecraft:$minecraft")
    implementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    implementation("dev.architectury:architectury:$architecturyApiVersion")
}

java {
    withSourcesJar()
    val javaVersion = project.property("javaVersion").toString()
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

/** 26.3 advancement backgrounds use sprite IDs; earlier versions use texture paths. */
tasks.processResources {
    filesMatching(listOf("data/genshinecraft/advancement/world_level_0.json",
            "data/genshinecraft/advancement/embrace_lies_accept_truth.json")) {
        filter { line: String ->
            line.replace("minecraft:textures/gui/advancements/backgrounds/stone.png",
                    "minecraft:gui/advancements/backgrounds/stone")
        }
    }
}
