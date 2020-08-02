package notifications.discord

import notifications.APIToken

data class DiscordToken(
    val id: Long,
    val token: String,
    val hookName : String
): APIToken(hookName)