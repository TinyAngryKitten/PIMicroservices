package notifications

data class DiscordToken(
    val id: Int,
    val token: String,
    val hookName: String
)