package me.agaman.kotlinfullstack.route

enum class ApiRoute(val path: String) {
    LOGIN("login"),
    LOGOUT("logout"),

    USER_CREATE("user/create"),
    USERS_LIST("user/list"),
}
