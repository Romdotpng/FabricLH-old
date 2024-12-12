val lwjglVersion = "3.3.2"

plugins {
    id("fabric-loom")
    kotlin("jvm").version(System.getProperty("kotlin_version"))
    id("com.github.johnrengelman.shadow").version("7.1.2")
}
base { archivesName.set(project.extra["archives_base_name"] as String) }
version = project.extra["mod_version"] as String
group = project.extra["maven_group"] as String

val library : Configuration by configurations.creating

repositories {
    mavenCentral()
}
configurations {
    shadow.get().extendsFrom(library)
    implementation.get().extendsFrom(library)
}
dependencies {
    minecraft("com.mojang", "minecraft", project.extra["minecraft_version"] as String)
    mappings("net.fabricmc", "yarn", project.extra["yarn_mappings"] as String, null, "v2")
    modImplementation("net.fabricmc", "fabric-loader", project.extra["loader_version"] as String)
    modImplementation("net.fabricmc.fabric-api", "fabric-api", project.extra["fabric_version"] as String)
    modImplementation("net.fabricmc", "fabric-language-kotlin", project.extra["fabric_language_kotlin_version"] as String)

    library("org.lwjgl", "lwjgl-nanovg", lwjglVersion)
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-linux-arm64")
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-linux-arm32")
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-linux")
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-macos-arm64")
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-windows-arm64")
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-windows-x86")
    library("org.lwjgl", "lwjgl-nanovg", classifier = "natives-windows")
}
loom {
    runConfigs.configureEach {
        ideConfigGenerated(true)
    }
}
tasks {
    val javaVersion = JavaVersion.toVersion((project.extra["java_version"] as String).toInt())
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
        options.release.set(javaVersion.toString().toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions { jvmTarget = javaVersion.toString() } }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName.get()}" } } }
    shadowJar {
        configurations = listOf(project.configurations.shadow.get())

        dependencies {
            exclude {
                it.moduleGroup == "org.slf4j" || it.moduleGroup == "com.google"
            }
        }

        archiveClassifier.set("shadow")
    }
    processResources {
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.extra["mod_version"] as String, "fabricloader" to project.extra["loader_version"] as String, "fabric_api" to project.extra["fabric_version"] as String, "fabric_language_kotlin" to project.extra["fabric_language_kotlin_version"] as String, "minecraft" to project.extra["minecraft_version"] as String, "java" to project.extra["java_version"] as String)) }
        filesMatching("*.mixins.json") { expand(mutableMapOf("java" to project.extra["java_version"] as String)) }
    }
    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        withSourcesJar()
    }
    task<Exec>("refmapper") {
        commandLine(
            "java",
            "-jar",
            "refmapper-2.1.jar",
            "build/libs/${base.archivesName.get()}-$version.jar",
            "build/libs/${base.archivesName.get()}-$version-refmap.jar",
            "refmapper/tiny",
            ".gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-project-root/${project.extra["minecraft_version"] as String}-net.fabricmc.yarn.${(project.extra["minecraft_version"] as String).replace('.', '_')}.${project.extra["yarn_mappings"] as String}-v2/minecraft-merged-project-root-${project.extra["minecraft_version"] as String}-net.fabricmc.yarn.${(project.extra["minecraft_version"] as String).replace('.', '_')}.${project.extra["yarn_mappings"] as String}-v2.jar"
        )
    }
}
