package me.agaman.kotlinfullstack.route

enum class Route(val path: String) {
    API("api"),
    CSRF("api/csrf"),
    LOGIN("api/login"),
    LOGOUT("api/logout"),
    STATIC("static"),
}
