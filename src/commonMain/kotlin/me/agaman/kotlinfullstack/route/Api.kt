package me.agaman.kotlinfullstack.route

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("api")
class Api {
    @Serializable
    @Resource("login")
    class Login(val parent: Api = Api())

    @Serializable
    @Resource("logout")
    class Logout(val parent: Api = Api())
}
