package notifications

import mu.KotlinLogging
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named


private val logger = KotlinLogging.logger{}

class DiscordNotifications : NotificationSender, KoinComponent {
  val host = "https://discordapp.com"

  val jsonType: MediaType = "application/json; charset=utf-8".toMediaTypeOrNull()!!
  val client : OkHttpClient by inject()
  val discordToken: DiscordToken by inject()

  override fun notify(notification: Notification) {
    val id : Int = discordToken.id
    val token : String = discordToken.token

    val path : String = "/api/webhooks/$id/$token"

    val request: Request = Request.Builder()
        .url(host+path)
        .post( createRequestBodyFromNotification(notification) )
        .build()

    client
        .newCall(request)
        .execute()
        .use { response ->
          logger.info{"response: "+response.body.toString()}
        }
  }


  private fun createRequestBodyFromNotification(notification: Notification) = RequestBody.create(jsonType, """
      {
        "username": "${notification.senderName}",
        "avatar_url": "",
        "content": "${notification.title}\n${notification.title}"
      }
    """.trimIndent())
}