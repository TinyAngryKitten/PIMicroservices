package config

import com.natpryce.konfig.*
import org.koin.dsl.module
import java.io.File

val host by stringType
val ip by intType

val konfigModule = module {
    single{
        ConfigurationProperties.systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationProperties.fromFile(File("defaults.properties"))
    }
}