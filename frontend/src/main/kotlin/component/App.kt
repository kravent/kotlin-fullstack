package component

import component.login.loginInterceptor
import component.store.storeProvider
import materialui.styles.createMuiTheme
import materialui.styles.themeprovider.themeProvider
import react.RBuilder
import react.ReactElement
import react.router.dom.browserRouter

private val theme = createMuiTheme {  }

fun RBuilder.app(): ReactElement {
    return storeProvider {
        themeProvider(theme) {
            loginInterceptor {
                browserRouter {
                    appRouter()
                }
            }
        }
    }
}
