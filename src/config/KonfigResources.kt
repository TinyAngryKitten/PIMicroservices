package config

import arrow.core.Try
import arrow.core.getOrDefault
import com.natpryce.konfig.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import wakeonlan.MachineID
import java.io.File

val host by stringType
val ip by intType
val machines by stringType

val konfigModule = module {

    //exract machine ids from config
    single {

        Try{ get<Configuration>()[machines] }
            .map { it.split(",") }
            .map { it.map {
                val parts = it.split("_")
                MachineID(parts[0],parts[1],parts[2])
            } }.getOrDefault { listOf() }
    }

    single{
        ConfigurationProperties.systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationProperties.fromFile(File("defaults.properties"))
    }
}