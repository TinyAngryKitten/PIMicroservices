package notifications.twilio

import notifications.APIToken

class TwilioToken(
    val projectSid : String,
    val numberSid : String,
    val number : String,
    val key : String,
    phoneName : String = "default"
) : APIToken(phoneName)