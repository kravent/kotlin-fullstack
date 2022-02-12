package component.login

import component.store.LoginStoreAction
import component.store.StoreState
import react.*
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction

external interface LoginInterceptorProps : PropsWithChildren

external interface LoginInterceptorStateProps : Props {
    var isLogged: Boolean
}

external interface LoginInterceptorDispatchProps : Props {
    var onLogin: (String) -> Unit
}

external interface InnerLoginInterceptorProps : LoginInterceptorProps, LoginInterceptorStateProps, LoginInterceptorDispatchProps

private val InnerLoginInterceptor = FC("InnerLoginInterceptor") { props: InnerLoginInterceptorProps ->
    if (props.isLogged) {
        props.children()
    } else {
        LoginPage {
            onUserLogged = { props.onLogin(it) }
        }
    }
}

private val LoginInterceptorConnector =
    rConnect<StoreState, RAction, WrapperAction, LoginInterceptorProps, LoginInterceptorStateProps, LoginInterceptorDispatchProps, InnerLoginInterceptorProps>(
        { state, _ ->
            isLogged = state.loggedUser != null
        },
        { dispatch, _ ->
            onLogin = { userName -> dispatch(LoginStoreAction(userName)) }
        }
    )

val LoginInterceptor = LoginInterceptorConnector { props ->
    child(createElement(InnerLoginInterceptor, props))
}
