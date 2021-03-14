package notifications.twilio

import arrow.syntax.function.memoize
import mu.KotlinLogging
import notifications.Notification
import notifications.NotificationSender
import notifications.TokenStorage
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger{}

class TwilioNotifications : NotificationSender, KoinComponent {
  private val client : OkHttpClient by inject()
  private val tokenStorage : TokenStorage by inject()

  override fun notify(notification: Notification, channel: String) =
      tokenStorage.fetchAll<PhoneNumber>()
          .filterNotNull()
          .forEach {
            sendNotification(notification, fetchToken("default")!!, it.number)
          }

  private fun sendNotification(notification: Notification, token: TwilioToken, receiver : String) =
      client.newCall(
          Request.Builder()
              .url(buildUrl(token))
              .header("Authorization", Credentials.basic(token.numberSid,token.key))
              .post(createRequestbody(notification,token, receiver))
              .build()
      ).execute()
      .use { response ->
        logger.info{"sms response: ${response.code}: "+response.body?.string()}
      }

  private fun createRequestbody(notification: Notification, token : TwilioToken, receiver : String) =
      FormBody.Builder()
          .add("To",receiver)
          .add("From", token.number)
          .add("Body", notification.body)
          .build()

  val fetchToken = { channel: String ->
    tokenStorage.fetchToken<TwilioToken>(channel)
  }.memoize()

  private fun buildUrl(token : TwilioToken) =
      "https://api.twilio.com/2010-04-01/Accounts/${token.projectSid}/Messages.json"
}