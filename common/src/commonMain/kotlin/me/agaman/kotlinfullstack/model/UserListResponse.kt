package me.agaman.kotlinfullstack.model

import kotlinx.serialization.Serializable

@Serializable
data class UserListResponse(
    val users: List<String>
)
