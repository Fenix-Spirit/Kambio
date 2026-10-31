plugins {
	alias(libs.plugins.kotlinMultiplatform)
	alias(libs.plugins.kotlinSerialization)
}

kotlin {
	jvmToolchain(21)
	jvm()
	js(IR) { browser() }

	sourceSets {
		commonMain.dependencies {
			implementation(libs.kotlinx.serialization.json)
		}
	}
}