/*
 * Copyright 2024 Angel Studio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.angel.soundtap

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SoundTapApplication : Application(), ImageLoaderFactory {
	override fun newImageLoader(): ImageLoader {
		return ImageLoader.Builder(this)
			.crossfade(true)
			.memoryCache {
				MemoryCache.Builder(this)
					.maxSizePercent(0.25)
					.build()
			}
			.diskCache {
				DiskCache.Builder()
					.directory(this.cacheDir.resolve("image_cache"))
					.maxSizePercent(0.02)
					.build()
			}
			.build()
	}
}
