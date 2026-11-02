plugins {
	alias(libs.plugins.kotlinJvm) apply false
	alias(libs.plugins.kotlinMultiplatform) apply false
	alias(libs.plugins.kotlinSerialization) apply false
	alias(libs.plugins.ktor) apply false
}

allprojects {
	group = "com.spiritfenix.kambio"
	version = "0.3.0"
}