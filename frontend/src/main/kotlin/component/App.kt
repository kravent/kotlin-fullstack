package component

import com.ccfraser.muirwik.components.mThemeProvider
import com.ccfraser.muirwik.components.styles.createMuiTheme
import component.login.loginInterceptor
import component.store.storeProvider
import react.RBuilder
import react.ReactElement
import react.router.dom.browserRouter

val createMuiTheme = createMuiTheme().apply {
    palette.type = "light"
}

fun RBuilder.app(): ReactElement {
    return storeProvider {
        mThemeProvider(createMuiTheme) {
            loginInterceptor {
                browserRouter {
                    appRouter()
                }
            }
        }
    }
}
