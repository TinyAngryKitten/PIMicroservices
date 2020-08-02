package notifications.discord

import mu.KotlinLogging
import notifications.Notification
import notifications.NotificationSender
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import util.append
import util.prepend


private val logger = KotlinLogging.logger{}

class DiscordNotifications : NotificationSender, KoinComponent {
  val host = "https://discordapp.com"

  val jsonType: MediaType = "application/json; charset=utf-8".toMediaTypeOrNull()!!
  val client : OkHttpClient by inject()

  val discordToken: DiscordToken by inject()
  val id = discordToken.id
  val token = discordToken.token
  val path = "/api/webhooks/$id/$token"


  override fun notify(notification: Notification) {
    val request: Request = Request.Builder()
        .url(host+path)
        .post( createRequestBodyFromNotification(notification) )
        .build()

    client
        .newCall(request)
        .execute()
        .use { response ->
          logger.info{"response: ${response.code}: "+response.body.toString()}
        }
  }

  private fun buildTitle(notification: Notification) =
      if(notification.title.isEmpty()) ""
      else "**${notification.title}**"

  private fun buildBody(notification: Notification) =
      notification.body

  private fun createRequestBodyFromNotification(notification: Notification) = RequestBody.create(jsonType, """
      {
        "username": "${notification.senderName?:""}",
        "avatar_url": "",
        "content": "${buildTitle(notification) + buildBody(notification)}"
      }
    """.trimIndent())
}