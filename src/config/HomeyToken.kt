package config

class HomeyToken(name : String = defaultName, val token : String) : Secret(defaultName) {
    companion object {
        val defaultName = "default"
    }
}