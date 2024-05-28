// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.kotlin.ksp) apply false
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.jetbrains.kotlin.android) apply false
	alias(libs.plugins.dagger.hilt.android) apply false
	alias(libs.plugins.google.services) apply false
	alias(libs.plugins.serialization) apply false

	// CI plugins
	alias(libs.plugins.ci.detekt)
	alias(libs.plugins.ci.ktlint)
	alias(libs.plugins.ci.spotless)
}

val ktlintVersion: String = libs.versions.pinterest.ktlint.get()

subprojects {
	apply {
		plugin("io.gitlab.arturbosch.detekt")
		plugin("org.jlleitschuh.gradle.ktlint")
		plugin("com.diffplug.spotless")
	}

	ktlint {
		debug.set(false)
		version.set(ktlintVersion)
		verbose.set(true)
		android.set(false)
		outputToConsole.set(true)
		ignoreFailures.set(false)
		enableExperimentalRules.set(true)
		filter {
			exclude("**/generated/**")
			include("**/kotlin/**")
		}
	}

	spotless {
		kotlin {
			target(
				fileTree(
					mapOf(
						"dir" to ".",
						"include" to listOf("**/*.kt"),
						"exclude" to listOf("**/build/**", "**/spotless/*.kt")
					)
				)
			)
			trimTrailingWhitespace()
			indentWithSpaces()
			endWithNewline()
			val delimiter = "^(package|object|import|interface|internal|@file|//startfile)"
			val licenseHeaderFile = rootProject.file("spotless/copyright.kt")
			licenseHeaderFile(licenseHeaderFile, delimiter)
		}
	}

	tasks {
		withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
			kotlinOptions {
				// Treat all Kotlin warnings as errors (disabled by default)
				// allWarningsAsErrors = project.hasProperty("warningsAsErrors") ? project.warningsAsErrors : false
				// Opt-in to experimental compose APIs
				freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")

				jvmTarget = "17"
			}
		}

		withType<Test>().configureEach {
			jvmArgs = listOf(
				"-Dkotlintest.tags.exclude=Integration,EndToEnd,Performance",
			)
			testLogging {
				events("passed", "skipped", "failed")
			}
			testLogging.showStandardStreams = true
			useJUnitPlatform()
		}
	}
}

tasks {

	withType<Test>().configureEach {
		jvmArgs = listOf(
			"-Dkotlintest.tags.exclude=Integration,EndToEnd,Performance",
		)
		testLogging {
			events("passed", "skipped", "failed")
		}
		testLogging.showStandardStreams = true
		useJUnitPlatform()
	}

	register<io.gitlab.arturbosch.detekt.Detekt>("templateDetekt") {
		description = "Runs a custom detekt build."
		setSource(files("src/main/kotlin", "src/test/kotlin"))
		config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
		debug = true
		reports {
			xml.required.set(true)
			xml.outputLocation.set(file("build/reports/detekt/detekt.xml"))
			html.required.set(true)
			txt.required.set(true)
		}
		include("**/*.kt")
		include("**/*.kts")
		exclude("resources/")
		exclude("build/")
		include("**/*.kt")
		include("**/*.kts")
		exclude(".*/resources/.*")
		exclude(".*/build/.*")
		exclude("/versions.gradle.kts")
		exclude("buildSrc/settings.gradle.kts")
	}
}

buildscript {
	repositories {
		google()
		mavenCentral()
	}
	dependencies {
		classpath(libs.build.gradle)
	}
}