plugins {
	alias(libs.plugins.kotlinJvm)
	alias(libs.plugins.kotlinSerialization)
	alias(libs.plugins.ktor)
	application
}

kotlin { jvmToolchain(21) }

application {
	mainClass.set("com.spiritfenix.kambio.ApplicationKt")
}

dependencies {
	implementation(project(":shared"))
	implementation(libs.ktor.server.core)
	implementation(libs.ktor.server.netty)
	implementation(libs.ktor.server.content.negotiation)
	implementation(libs.ktor.serialization.json)
	implementation(libs.logback)
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.server.status.pages)
}
ktor {
	fatJar {
		archiveFileName.set("kambio.jar")
	}
}