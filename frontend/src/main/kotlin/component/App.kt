package component

import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.createMuiTheme
import component.login.loginInterceptor
import component.materialui.mRouteLink
import component.navbar.navBar
import component.store.storeProvider
import component.user.userManager
import react.RBuilder
import react.ReactElement
import react.dom.div
import react.dom.h1
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch

val createMuiTheme = createMuiTheme().apply {
    palette.type = "light"
}

fun RBuilder.app(): ReactElement {
    return storeProvider {
        mThemeProvider(createMuiTheme) {
            loginInterceptor {
                browserRouter {
                    switch {
                        route("/", exact = true) {
                            div {
                                navBar()
                                div {
                                    mRouteLink("/users") { +"Users" }
                                }
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
        }
    }
}
