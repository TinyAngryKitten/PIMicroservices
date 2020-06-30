package wakeonlan

import arrow.core.*

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Class for sending magic packets to a host with a given ip address and mac address, to wake it up(WakeOnLan)
 */
class WoLClient {
    //The length of the MAC address.
    private val MAC_LENGTH = 6

    //Magic packets must be 102 bytes. Length of the UDP packet.
    private val UDP_MULTIPLIER = 16

    private fun sendPacket(macAsBytes : ByteArray, host:String, port:Int): WoLResult {
        InetAddress.getByName(host)?.let { address ->

            val bytes = ByteArray(MAC_LENGTH + UDP_MULTIPLIER * macAsBytes.size) {
                    i->
                when(i) {//will with 0xff until 16,  then repeat mac address until array is full
                    in 0 until MAC_LENGTH -> 0xff.toByte()
                    else -> macAsBytes[i%macAsBytes.size]
                }
            }

            val packet = DatagramPacket(bytes, bytes.size, address, port)

            DatagramSocket().apply {
                send(packet)
                close()
            }
        } ?: return WoLResult.InvalidHostAddress

        return WoLResult.Success
    }

    private fun macAddressToBytes(macAddress : String) : Option<ByteArray> {
        val addresNumbers = macAddress.split("-")

        return if(addresNumbers.size != 6) Option.empty()
        else Try {
            ByteArray(6) { i->
                Integer.parseInt(addresNumbers[i],16).toByte()
            }
        }.toOption()
    }

    fun wake(mac:String,host:String,port:Int = 9) : WoLResult {
        return when (val addressAsBytes = macAddressToBytes(mac)) {
            is Some -> sendPacket(addressAsBytes.t, host, port)
            is None -> WoLResult.InvalidMacAddresss
        }
    }
}
