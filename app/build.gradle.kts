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

import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
	alias(libs.plugins.compose.compiler)
	alias(libs.plugins.kotlin.ksp)
	alias(libs.plugins.android.application)

	alias(libs.plugins.google.services)
	alias(libs.plugins.firebase.crashlytics)
	alias(libs.plugins.firebase.perf)

	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.dagger.hilt.android)
	alias(libs.plugins.serialization)
}

android {
	namespace = "fr.angel.soundtap"
	compileSdk = 34

	ndkVersion = "27.0.11718014"

	defaultConfig {
		applicationId = "fr.angel.soundtap"
		minSdk = 30
		targetSdk = 34
		versionCode = 40
		versionName = "1.1.4"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {

		release {
			isMinifyEnabled = true
			isShrinkResources = true

			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)

			configure<CrashlyticsExtension> {
				mappingFileUploadEnabled = true
				nativeSymbolUploadEnabled = true
				unstrippedNativeLibsDir = "build/intermediates/merged_native_libs/release/out/lib"
			}

			ndk {
				debugSymbolLevel = "FULL"
			}
		}
	}
	lint {
		warningsAsErrors = false
		abortOnError = true
		baseline = file("lint-baseline.xml")
		checkReleaseBuilds = false
		ignoreTestSources = true
		checkDependencies = true
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}
	kotlinOptions {
		jvmTarget = "17"
	}
	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.14"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

composeCompiler {
	enableStrongSkippingMode = true

	reportsDestination = layout.buildDirectory.dir("compose_compiler")
	// stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.foundation)
	implementation(libs.androidx.animation)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.palette.ktx)
	implementation(libs.lifecycle.common)
	implementation(libs.lifecycle.service)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)

	implementation(libs.androidx.core.splashscreen)
	implementation(libs.androidx.material.icons.extended)
	implementation(libs.accompanist.permissions)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.lottie.compose)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.datastore.preferences)
	implementation(libs.gson)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.accompanist.drawablepainter)
	implementation(kotlin("reflect"))
	implementation(libs.coil.compose)

	implementation(libs.hilt.android.android)
	implementation(libs.androidx.hilt.navigation.fragment)
	implementation(libs.androidx.hilt.navigation.compose)
	ksp(libs.hilt.android.compiler)

	implementation(platform(libs.firebase.bom))
	implementation(libs.firebase.analytics)
	implementation(libs.firebase.crashlytics)
	implementation(libs.firebase.perf)

	implementation(libs.androidx.datastore)
	implementation(libs.kotlinx.collections.immutable)
	implementation(libs.kotlinx.serialization.json)

	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.appcompat.resources)
}
