package wakeonlan

import arrow.core.Option
import arrow.core.Try

data class MachineID constructor(
    val name : String,
    val macAddress : String,
    val host : String
) {
    val addressAsBytes = macAddressToBytes()

    private fun macAddressToBytes() : Option<ByteArray> {
        val addresNumbers = if(macAddress.contains("-"))
                            macAddress.split("-")
                            else macAddress.split(":")

        return if(addresNumbers.size != 6) Option.empty()
        else Try {
            ByteArray(6) { i->
                Integer.parseInt(addresNumbers[i],16).toByte()
            }
        }.toOption()
    }
}