import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
	alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
	js {
		browser {
			commonWebpackConfig {
				outputFileName = "web.js"
				devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
					port = 3000,
					proxy = mutableListOf(
						KotlinWebpackConfig.DevServer.Proxy(
							context = mutableListOf("/api"),
							target = "http://localhost:8080",
						)
					),
				)
			}
		}
		binaries.executable()
	}
	sourceSets {
		jsMain.dependencies {
			implementation(project.dependencies.enforcedPlatform(libs.kotlin.wrappers.bom))
			implementation(libs.kotlin.react)
			implementation(libs.kotlin.react.dom)
			implementation(libs.ktor.client.core)
			implementation(libs.ktor.client.js)
			implementation(libs.ktor.client.content.negotiation)
			implementation(libs.ktor.serialization.json)
			implementation(project(":shared"))
		}
	}
}