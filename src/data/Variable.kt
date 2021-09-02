package data

import kotlinx.serialization.Serializable

@Serializable
data class Variable(
        val id : String,
        val name : String,
        val type : String,
        val value : Boolean? = null
)