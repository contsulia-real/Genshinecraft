plugins {
    id("dev.architectury.loom")
    id("dev.architectury.loom-no-remap") apply false
    id("architectury-plugin")
    id("com.gradleup.shadow") apply false
}

val minecraft = stonecutter.current.version
val modId = project.property("modId").toString()
val modVersion = project.property("modVersion").toString()

version = "$modVersion+$minecraft"
base { archivesName.set("$modId-common") }

architectury.common(if (minecraft == "1.20.1") listOf("fabric", "forge") else listOf("fabric", "neoforge"))

loom {
    silentMojangMappingsLicense()
}

val fabricLoaderVersion = project.property("fabricLoaderVersion").toString()
val architecturyApiVersion = project.property("architecturyApiVersion").toString()

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("dev.architectury:architectury:$architecturyApiVersion")
}

java {
    withSourcesJar()
    val javaVersion = project.property("javaVersion").toString()
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
