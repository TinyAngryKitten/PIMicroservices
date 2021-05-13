package config

data class HomeyToken(val value : String) : Secret(secretName) {
    companion object {
        val secretName = "default"
    }
}