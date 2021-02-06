package config

import io.vertx.ext.consul.ConsulClient
import io.vertx.ext.consul.ServiceOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import mu.KotlinLogging
import org.koin.core.KoinComponent
import org.koin.core.inject

private val logger = KotlinLogging.logger{}

class ConsulServiceDiscovery : ServiceDiscovery, KoinComponent {
  val consulClient : ConsulClient by inject()
  val consulOptions : ServiceOptions by inject()

  override fun registerService() {
    consulClient.registerService(consulOptions) {
      if(it.succeeded()) logger.info { "Service registered in consul" }
      else logger.error{"Service could not be registered in consul: ${it.cause()}"}
    }
  }

  override suspend fun fetchServiceByName(name: String) = callbackFlow<Service?> {
    /*consulClient.catalogServices{
      runBlocking {
        if (it.failed()) logger.error { "Unable to fetch services from consul: ${it.cause()}" }

        val consulService = it
            .result()
            ?.list
            ?.find { service: io.vertx.ext.consul.Service ->
              service.name == name
            }

        send(Service(
            name,
            consulService?.address ?: "",
            consulService?.port ?: 0
        ))
      }
    }*/
  }
}