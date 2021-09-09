package config

class HomeyToken( val token : String, name : String = defaultName) : Secret(defaultName) {
    companion object {
        val defaultName = "default"
    }
}