package me.agaman.kotlinfullstack.model

import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    val users: List<String>
)
