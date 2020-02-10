package component

import component.user.userManager
import react.RBuilder
import react.dom.div
import react.dom.h1
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.routeLink
import react.router.dom.switch

fun RBuilder.app() = browserRouter {
    switch {
        route("/", exact = true) {
            div {
                h1 { +"Hello" }
                routeLink("/users") { +"Users" }
            }
        }
        route("/users", exact = true) {
            userManager()
        }
        route("*") {
            h1 { +"ERROR: Page not found" }
        }
    }
}
