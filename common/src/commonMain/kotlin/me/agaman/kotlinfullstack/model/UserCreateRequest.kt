package me.agaman.kotlinfullstack.model

import kotlinx.serialization.Serializable

@Serializable
data class UserCreateRequest(
    val userName: String
)
