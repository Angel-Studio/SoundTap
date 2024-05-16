package fr.angel.soundtap.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotificationService : NotificationListenerService() {

	override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
		super.onNotificationPosted(statusBarNotification)
	}
}