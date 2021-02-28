package notifications

interface NotificationSender {
  fun notify(notification: Notification, channel: String = "General")
}