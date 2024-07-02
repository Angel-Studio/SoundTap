/*
 *
 *  * Copyright (c) 2024 Angel Studio
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import com.google.gms.googleservices.GoogleServicesTask

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
	alias(libs.plugins.compose.compiler) apply false
	alias(libs.plugins.kotlin.ksp) apply false
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.google.services) apply false
	alias(libs.plugins.jetbrains.kotlin.android) apply false
	alias(libs.plugins.dagger.hilt.android) apply false
	alias(libs.plugins.serialization) apply false
	alias(libs.plugins.firebase.crashlytics) apply false

	// CI plugins
	//alias(libs.plugins.ci.ktlint)
}

val ktlintVersion: String = "1.2.1"

subprojects {

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
