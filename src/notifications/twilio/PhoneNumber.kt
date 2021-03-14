package notifications.twilio

import notifications.APIToken

class PhoneNumber(
    name : String,
    val number : String
) : APIToken(name)