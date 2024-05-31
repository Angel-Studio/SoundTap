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

package fr.angel.soundtap

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

class NotificationHelper
	@Inject
	constructor(
		@ApplicationContext private val context: Context,
	) {
		private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		companion object {
			const val CHANNEL_ID_SLEEP_TIMER = "sleep_timer"
			const val NOTIFICATION_ID = 1
		}

		init {
			val channel =
				NotificationChannel(
					// id =
					CHANNEL_ID_SLEEP_TIMER,
					// name =
					context.getString(R.string.app_sleep_timer),
					// importance =
					NotificationManager.IMPORTANCE_MIN,
				)
			notificationManager.createNotificationChannel(channel)
		}

		fun showSleepTimerNotification(millisUntilFinished: Long) {
			val notification =
				NotificationCompat.Builder(context, CHANNEL_ID_SLEEP_TIMER)
					.setContentTitle(context.getString(R.string.app_sleep_timer))
					.setContentText(context.getString(R.string.app_sleep_timer_message))
					.setSilent(true)
					.setOngoing(true)
					.setUsesChronometer(true)
					.setChronometerCountDown(true)
					.setWhen(System.currentTimeMillis() + millisUntilFinished)
					.setShowWhen(true)
					.setOnlyAlertOnce(true)
					.setSmallIcon(R.drawable.twotone_timer_24)
					.addAction(
						// icon =
						R.drawable.twotone_stop_circle_24,
						// title =
						context.getString(R.string.stop),
						// intent =
						GlobalHelper.createStopSleepTimerIntent(context),
					).addAction(
						// icon =
						R.drawable.twotone_more_time_24,
						// title =
						context.getString(R.string.app_sleep_timer_add_time_15_min),
						// intent =
						GlobalHelper.createAddTimeSleepTimerIntent(context, 15 * 60 * 1000),
					).setContentIntent(GlobalHelper.createNotificationOpenAppIntent(context))
					.build()

			notificationManager.notify(NOTIFICATION_ID, notification)
		}

		fun cancelSleepTimerNotification() {
			notificationManager.cancel(NOTIFICATION_ID)
		}
	}

@Module
@InstallIn(SingletonComponent::class)
object NotificationHelperModule {
	@Provides
	@Singleton
	fun provideNotificationHelper(
		@ApplicationContext context: Context,
	): NotificationHelper {
		return NotificationHelper(context)
	}
}
