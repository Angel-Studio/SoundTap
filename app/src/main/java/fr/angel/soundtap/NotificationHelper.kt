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
					"Sleep Timer",
					// importance =
					NotificationManager.IMPORTANCE_MIN,
				)
			notificationManager.createNotificationChannel(channel)
		}

		fun showSleepTimerNotification(millisUntilFinished: Long) {
			val notification =
				NotificationCompat.Builder(context, CHANNEL_ID_SLEEP_TIMER)
					.setContentTitle("Sleep Timer")
					.setContentText("Music will stop when the timer ends.")
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
						"Stop",
						// intent =
						GlobalHelper.createStopSleepTimerIntent(context),
					).addAction(
						// icon =
						R.drawable.twotone_more_time_24,
						// title =
						"Add 15 minutes",
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
