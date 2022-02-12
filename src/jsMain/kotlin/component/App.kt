package component

import component.login.LoginInterceptor
import component.store.Store
import react.FC
import react.Props
import react.redux.Provider

val App = FC<Props> {
    Provider {
        store = Store
        LoginInterceptor {
            AppRouter {}
        }
    }
}
