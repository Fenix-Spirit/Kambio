import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotlinSerialization)
}

kotlin {
	jvmToolchain(21)
	jvm()
	this.js { browser() }

	sourceSets {
		commonMain.dependencies {
			api(libs.kotlinx.serialization.json)
		}
	}
}