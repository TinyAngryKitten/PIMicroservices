package notifications

data class Notification(
    val body : String,
    val title : String = "",
    val senderName: String = "",
    val senderIconUrl: String =""
)