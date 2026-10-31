plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js(IR) {
		browser()
		binaries.executable()
	}
	sourceSets {
		jsMain.dependencies {
			implementation(project(":shared"))
		}
	}
}