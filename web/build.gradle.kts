plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	this.js {
		browser()
		binaries.executable()
	}
	sourceSets {
		jsMain.dependencies {
			implementation(project(":shared"))
		}
	}
}