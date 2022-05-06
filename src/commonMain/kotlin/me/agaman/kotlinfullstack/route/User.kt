package me.agaman.kotlinfullstack.route

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("user")
class User(val parent: Api = Api()) {
    @Serializable
    @Resource("create")
    class Create(val parent: User = User())

    @Serializable
    @Resource("list")
    class List(val parent: User = User())
}
