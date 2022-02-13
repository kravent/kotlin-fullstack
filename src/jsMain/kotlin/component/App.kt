package component

import component.login.LoginInterceptor
import component.store.Store
import react.redux.Provider
import utils.NC

val App by NC {
    Provider {
        store = Store
        LoginInterceptor {
            AppRouter {}
        }
    }
}
