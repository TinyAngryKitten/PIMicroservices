package wakeonlan

sealed class WoLResult {
    object InvalidMacAddresss : WoLResult()
    object InvalidHostAddress : WoLResult()
    object Success : WoLResult()
}