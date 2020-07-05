package wmi

import java.io.InputStreamReader
import java.util.ArrayList

private fun getShellOutput(cmd: String): String {
    val p = Runtime.getRuntime().exec(cmd)
    val isr = InputStreamReader(p.inputStream)

    val sb = StringBuilder()
    while (true) {
        val x = isr.read()
        if (x != -1) {
            sb.append(x.toChar())
        } else
            break
    }

    isr.close()
    p.destroy()

    return sb.toString()
}

@Throws(Exception::class)
private fun getValuesFromWMI(): ArrayList<Array<String>> {
    val out = getShellOutput("powershell.exe get-wmiobject -namespace root\\OpenHardwareMonitor -query 'SELECT Value,Name,SensorType FROM Sensor'").replace("\r\n\r\n__GENUS          : 2\r\n__CLASS          : Sensor\r\n__SUPERCLASS     : \r\n__DYNASTY        : \r\n__RELPATH        : \r\n__PROPERTY_COUNT : 3\r\n__DERIVATION     : {}\r\n__SERVER         : \r\n__NAMESPACE      : \r\n__PATH           : \r\n", "")
    val returnable = ArrayList<Array<String>>()

    val x = out.split("PSComputerName {3}:".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

    for (i in 0 until x.size - 1) {
        val cd = x[i].split("\r\n".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
        returnable.add(arrayOf(cd[0].substring(cd[0].indexOf("Name             : ")).replace("Name             : ", ""), cd[1].substring(cd[1].indexOf("SensorType       : ")).replace("SensorType       : ", ""), cd[2].substring(cd[2].indexOf("Value            : ")).replace("Value            : ", "")))
    }

    return returnable
}

fun getWMIData() = parseWMIData(getValuesFromWMI())

private fun parseWMIData(list : ArrayList<Array<String>>) : Map<String, Any?> {

    var cpuLoad : String? =null
    var cpuTemp : String? =null
    var usedRam : String? =null
    var availableRam : String? =null
    var cpuFan : String? =null

    var gpuLoad : String? =null
    var gpuTemp : String? =null
    var usedVRam : String? =null
    var totalVRam : String? =null
    var gpuFan : String? =null

    list.forEach{each ->
        if (each[0] == "CPU Total" && each[1] == "Load")
            cpuLoad = each[2]
        else if (each[0] == "GPU Core" && each[1] == "Load")
            cpuLoad = each[2]
        else if (each[0] == "CPU Fan" && each[1] == "Fan")
            cpuFan = each[2]
        else if (each[0] == "GPU" && each[1] == "Fan")
            gpuFan = each[2]
        else if (each[0] == "CPU Package" && each[1] == "Temperature")
            cpuTemp = each[2]
        else if (each[0] == "GPU Core" && each[1] == "Temperature")
            gpuTemp = each[2]
        else if (each[0] == "GPU Memory Total" && each[1] == "SmallData")
            totalVRam = each[2]
        else if (each[0] == "GPU Memory Used" && each[1] == "SmallData")
            usedVRam = each[2]
        else if (each[0] == "Used Memory" && each[1] == "Data")
            usedRam = each[2]
        else if (each[0] == "Available Memory" && each[1] == "Data")
            availableRam = each[2]
    }

        val result = mapOf(
                "cpu/load" to cpuLoad,
                "cpu/temp" to cpuTemp,
                "cpu/usedRam" to usedRam,
                "cpu/totalRam" to "${( availableRam?.toDouble()?.plus(usedRam?.toDouble()?: 0.toDouble()) )}",
                "cpu/fan" to cpuFan,

                "gpu/load" to gpuLoad,
                "gpu/temp" to gpuTemp,
                "gpu/usedVRam" to usedVRam,
                "gpu/totalVRam" to totalVRam,
                "gpu/fan" to gpuFan
        ).filter { it.value != null}
    return result
}