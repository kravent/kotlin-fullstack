package component

import component.main.mainPage
import component.materialui.mRouteLink
import component.user.userManager
import react.RBuilder
import react.dom.h1
import react.router.dom.route
import react.router.dom.switch

fun RBuilder.appRouter() = switch {
    route("/", exact = true) {
        mainPage(title = "Home") {
            mRouteLink("/users") { +"Users" }
        }
    }
    route("/users", exact = true) {
        mainPage(title = "Users", backRoute = "/") {
            userManager()
        }
    }
    route("*") {
        h1 { +"ERROR: Page not found" }
    }
}
