package component

import component.login.loginInterceptor
import component.store.storeProvider
import materialui.styles.defaultMuiTheme
import materialui.styles.themeprovider.themeProvider
import react.RBuilder
import react.ReactElement
import react.router.dom.browserRouter

fun RBuilder.app(): ReactElement {
    return storeProvider {
        themeProvider(defaultMuiTheme) {
            loginInterceptor {
                browserRouter {
                    appRouter()
                }
            }
        }
    }
}
