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