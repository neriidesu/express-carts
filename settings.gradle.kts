pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	// These are not applied at this level, but we specify them here to set the versions.
	id("net.fabricmc.fabric-loom-remap").version("1.15-SNAPSHOT").apply(false)
	id("com.modrinth.minotaur").version("2.+").apply(false)
	id("com.github.breadmoirai.github-release").version("2.4.1").apply(false)
	id("me.modmuss50.mod-publish-plugin").version("1.1.0").apply(false)
}