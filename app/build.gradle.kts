plugins {
	alias(libs.plugins.kotlin.ksp)
	alias(libs.plugins.android.application)
	alias(libs.plugins.jetbrains.kotlin.android)
	alias(libs.plugins.dagger.hilt.android)
	alias(libs.plugins.google.services)
	alias(libs.plugins.firebase.crashlytics)
	alias(libs.plugins.firebase.perf)
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
		versionCode = 22
		versionName = "1.0.6"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {

		release {
			isMinifyEnabled = true
			isShrinkResources = true

			/*proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)*/

			ndk {
				debugSymbolLevel = "FULL"
			}
		}
	}
	lint {
		warningsAsErrors = true
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
		kotlinCompilerExtensionVersion = "1.5.13"
	}
	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
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
}