package fr.angel.soundtap.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.angel.soundtap.GlobalHelper
import fr.angel.soundtap.NotificationHelper
import javax.inject.Inject

class SleepTimerService : Service() {
	companion object {
		fun startService(
			context: Context,
			duration: Long,
		) {
			val intent = Intent(context, SleepTimerService::class.java)
			intent.putExtra(EXTRA_DURATION, duration)
			context.startService(intent)
		}

		fun cancelTimer(context: Context) {
			val intent = Intent(context, SleepTimerService::class.java)
			intent.action = ACTION_STOP
			context.startService(intent)
		}

		fun addTime(
			context: Context,
			duration: Long,
		) {
			val intent = Intent(context, SleepTimerService::class.java)
			intent.action = ACTION_ADD_TIME
			intent.putExtra(EXTRA_DURATION, duration)
			context.startService(intent)
		}

		const val TAG: String = "SleepTimerService"

		const val ACTION_STOP: String = "fr.angel.soundtap.service.SleepTimerService.STOP"
		const val ACTION_ADD_TIME: String = "fr.angel.soundtap.service.SleepTimerService.ADD_TIME"
		const val EXTRA_DURATION: String = "duration"

		var isRunning: Boolean by mutableStateOf(false)
		var remainingTime: Long by mutableLongStateOf(0)
	}

	@Inject
	lateinit var notificationHelper: NotificationHelper

	private var timer: CountDownTimer? = null
	private var duration: Long = 0

	override fun onStartCommand(
		intent: Intent?,
		flags: Int,
		startId: Int,
	): Int {
		val action = intent?.action
		when (action) {
			ACTION_STOP -> {
				if (!::notificationHelper.isInitialized) {
					Log.w(TAG, "notificationHelper is not initialized")
					stopSelf()
					return START_NOT_STICKY
				}
				notificationHelper.cancelSleepTimerNotification()
				stopSelf()
				return START_NOT_STICKY
			}

			ACTION_ADD_TIME -> {
				val extraDuration = intent.getLongExtra(EXTRA_DURATION, 0)
				duration += extraDuration
				timer?.cancel()
				startTimer(duration)
				return START_STICKY
			}
		}

		duration = intent?.getLongExtra(EXTRA_DURATION, 0) ?: 0

		while (!::notificationHelper.isInitialized) {
			Thread.sleep(100)
		}

		isRunning = true
		startTimer(duration)

		return START_STICKY
	}

	override fun onCreate() {
		super.onCreate()

		notificationHelper = NotificationHelper(this)
	}

	private fun startTimer(duration: Long) {
		timer =
			object : CountDownTimer(duration, 1000) {
				override fun onTick(millisUntilFinished: Long) {
					remainingTime = millisUntilFinished
					notificationHelper.showSleepTimerNotification(millisUntilFinished)
				}

				override fun onFinish() {
					GlobalHelper.stopMusic()
					notificationHelper.cancelSleepTimerNotification()
					stopSelf()
				}
			}.start()
	}

	override fun onBind(intent: Intent): IBinder? {
		return null
	}

	override fun onDestroy() {
		super.onDestroy()
		timer?.cancel()
		isRunning = false
	}
}
