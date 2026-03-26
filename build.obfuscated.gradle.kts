plugins {
    id("net.fabricmc.fabric-loom-remap")
    id("me.modmuss50.mod-publish-plugin")
}

val modVersion = "${property("mod.version")}${localBuildVersionSuffix().get()}"

version = "$modVersion+${sc.current.version}"
group = property("mod.group").toString()

base.archivesName = "${property("mod.id")}-fabric"

repositories {
    maven("https://maven.nucleoid.xyz")

    // https://github.com/xpple/BetterConfig/
    maven("https://maven.xpple.dev/maven2")
}

dependencies {
    fun modInclude(dep: Any): Dependency? = modImplementation(include(dep)!!)

    minecraft("com.mojang:minecraft:${sc.current.version}")
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric.loader")}")

    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric.api")}")

    modInclude("eu.pb4:polymer-core:${property("deps.polymer")}")
    modInclude("eu.pb4:polymer-resource-pack:${property("deps.polymer")}")
    modInclude("xyz.nucleoid:server-translations-api:${property("deps.server_translations")}")

    modInclude("dev.xpple:betterconfig-fabric:${property("deps.betterconfig")}")
}

tasks {
    processResources {
        val props = mapOf(
            "id" to project.property("mod.id"),
            "version" to project.version,
            "minecraft" to sc.current.version,
            "fabricloader" to project.property("deps.fabric.loader"),
            "fabricapi" to project.property("deps.fabric.api"),
            "java" to requiredJava.majorVersion
        )

        inputs.properties(props)

        filesMatching("fabric.mod.json") {
            expand(props)
        }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }

    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
//    accessWidenerPath = sc.process(
//        rootProject.file("src/main/resources/template.ct"),
//        "build/processed.ct"
//    )
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
        runDir = "../../run" // Shares the run directory between versions
    }

}

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

java {
    withSourcesJar()

    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
}

val changelogFile = file("CHANGELOG.md")
val changelogText = if (changelogFile.canRead()) changelogFile.readText() else ""
val readmeFile = file("README.md")
val readmeText = if (readmeFile.canRead()) readmeFile.readText() else ""

publishMods {
    changelog = changelogText
    type = STABLE
    file = tasks.remapJar.flatMap { it.archiveFile }
    displayName = "$modVersion for ${sc.current.version} Fabric"

    dryRun = !hasProperty("publish.release")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = providers.gradleProperty("publish.modrinth.id")
        minecraftVersions.add(providers.gradleProperty("publish.modrinth.minecraft"))

        projectDescription = readmeText

        requires("fabric-api")
        embeds("polymer")
        embeds("betterconfig")
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = providers.environmentVariable("GITHUB_REPOSITORY_ID").orElse(providers.gradleProperty("publish.github.repository"))
        commitish = providers.environmentVariable("GITHUB_REF").orElse(providers.gradleProperty("publish.github.commitish"))
    }
}

tasks.named("publishMods") {
    onlyIf {
        !changelogText.isBlank() && !readmeText.isBlank()
    }
}

// If we are not performing a release build (i.e. building in CI), try to get some info about the state of the Git repo
// to add to the version (so local builds can be easily distinguished from released builds).
private fun localBuildVersionSuffix(): Provider<String> {
    // git will give the (abbreviated) commit hash, suffixed with "-dirty" if the working dir is dirty.
    // If we can't exec git, fallback to just "-local"
    val gitCommitProvider = providers.exec {
        commandLine("git", "describe", "--always", "--dirty", "--exclude", "*")
    }.standardOutput.asText.map {
        "-${it.trim()}"
    }.orElse("-local")

    return providers.gradleProperty("publish.release").map {
        // If this is called, the "publish.release" property is present.
        // Return an empty string.
        return@map ""
    }.orElse(gitCommitProvider)
}
