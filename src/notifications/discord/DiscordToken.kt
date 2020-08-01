package notifications.discord

data class DiscordToken(
    val id: Long,
    val token: String,
    val hookName: String
)