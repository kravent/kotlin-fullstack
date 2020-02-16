package component

import component.store.storeProvider
import component.user.userManager
import react.RBuilder
import react.dom.div
import react.dom.h1
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.routeLink
import react.router.dom.switch

fun RBuilder.app() = storeProvider {
    browserRouter {
        switch {
            route("/", exact = true) {
                div {
                    navBar()
                    h1 { +"Hello" }
                    routeLink("/users") { +"Users" }
                }
            }
            route("/users", exact = true) {
                div {
                    navBar()
                    userManager()
                }
            }
            route("*") {
                h1 { +"ERROR: Page not found" }
            }
        }
    }
}
