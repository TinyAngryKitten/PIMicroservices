package config

import kotlinx.coroutines.flow.Flow

interface ServiceDiscovery {
  fun registerService()
  suspend fun fetchServiceByName(name : String) : Flow<Service?>
}

data class Service(
    val name: String,
    val address: String,
    val port: Int
)