package component.login

import component.store.LoginStoreAction
import component.store.StoreState
import react.*
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction

interface LoginInterceptorStateProps : RProps {
    var isLogged: Boolean
}

interface LoginInterceptorDispatchProps : RProps {
    var onLogin: (String) -> Unit
}

data class LoginInterceptorProps(
    val isLogged: Boolean,
    val onLogin: (String) -> Unit
) : RProps

val LoginInterceptor = rFunction("LoginInterceptor") { props: LoginInterceptorProps ->
    if (props.isLogged) {
        props.children()
    } else {
        loginPage(onUserLogged = { props.onLogin(it) })
    }
}

val LoginInterceptorConnector =
    rConnect<StoreState, RAction, WrapperAction, RProps, LoginInterceptorStateProps, LoginInterceptorDispatchProps, LoginInterceptorProps>(
        { state, _ ->
            isLogged = state.loggedUser != null
        },
        { dispatch, _ ->
            onLogin = { userName -> dispatch(LoginStoreAction(userName)) }
        }
    )

val ConnectedLoginInterceptor = LoginInterceptorConnector(LoginInterceptor)

fun RBuilder.loginInterceptor(handler: RHandler<RProps>) = ConnectedLoginInterceptor {
    handler()
}
