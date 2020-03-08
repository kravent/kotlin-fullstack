package component

import com.ccfraser.muirwik.components.mThemeProvider
import component.login.loginInterceptor
import component.store.storeProvider
import react.RBuilder
import react.ReactElement
import react.router.dom.browserRouter

fun RBuilder.app(): ReactElement {
    return storeProvider {
        mThemeProvider {
            loginInterceptor {
                browserRouter {
                    appRouter()
                }
            }
        }
    }
}
