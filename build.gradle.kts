import com.google.gms.googleservices.GoogleServicesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.kotlin.ksp) apply false
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.google.services) apply false
	alias(libs.plugins.jetbrains.kotlin.android) apply false
	alias(libs.plugins.dagger.hilt.android) apply false
	alias(libs.plugins.serialization) apply false
	alias(libs.plugins.firebase.crashlytics) apply false

	// CI plugins
	alias(libs.plugins.ci.ktlint)
}

val ktlintVersion: String = "1.2.1"

subprojects {
	apply {
		plugin("org.jlleitschuh.gradle.ktlint")
	}

	// https://github.com/firebase/firebase-android-sdk/issues/5962#:~:text=project.afterEvaluate%20%7B%0A%20%20%20%20tasks.withType%3CGoogleServicesTask%3E%20%7B%0A%20%20%20%20%20%20%20%20gmpAppId.set(project.layout.buildDirectory.file(%22%24name%2DgmpAppId.txt%22))%0A%20%20%20%20%7D%0A%7D
	project.afterEvaluate {
		tasks.withType<GoogleServicesTask> {
			gmpAppId.set(project.layout.buildDirectory.file("$name-gmpAppId.txt"))
		}
	}

	tasks.whenTaskAdded {
		if (name == "assembleRelease") {
			finalizedBy("uploadCrashlyticsSymbolFileRelease")
		}
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

	tasks {
		withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
			kotlinOptions {
				// Treat all Kotlin warnings as errors (disabled by default)
				// allWarningsAsErrors = project.hasProperty("warningsAsErrors") ? project.warningsAsErrors : false
				// Opt-in to experimental compose APIs
				freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")

				jvmTarget = "17"
			}
		}

		withType<Test>().configureEach {
			jvmArgs =
				listOf(
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
		jvmArgs =
			listOf(
				"-Dkotlintest.tags.exclude=Integration,EndToEnd,Performance",
			)
		testLogging {
			events("passed", "skipped", "failed")
		}
		testLogging.showStandardStreams = true
		useJUnitPlatform()
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
