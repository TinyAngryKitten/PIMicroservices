package config

import com.natpryce.konfig.*
import org.koin.dsl.module
import java.io.File

val host by stringType
val lookupInterval by intType
val users by stringType
val port by intType
val consulhost by stringType
val consulport by intType


val dbuser by stringType
val dbpassword by stringType
val dbport by intType
val dburl by stringType
val dbname by stringType


val konfigModule = module {
    single{
        ConfigurationProperties.systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationProperties.fromFile(File("defaults.properties"))
    }
}
