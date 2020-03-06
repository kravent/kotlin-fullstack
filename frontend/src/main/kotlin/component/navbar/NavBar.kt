package component.navbar

import ajax.Api
import component.store.LogoutStoreAction
import component.store.StoreState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.h1
import react.redux.rConnect
import react.router.dom.routeLink
import redux.RAction
import redux.WrapperAction
import styled.css
import styled.styledDiv

interface NavBarStateProps : RProps {
    var userName: String?
}

interface NavBarDispatchProps : RProps {
    var onLogout: () -> Unit
}

data class NavBarProps(
    val userName: String?,
    val onLogout: () -> Unit
) : RProps

val NavBar = rFunction("NavBarComponent") { props: NavBarProps ->
    fun doLogout() {
        MainScope().launch {
            Api.logout()
            props.onLogout()
        }
    }

    styledDiv {
        css {
            borderBottomStyle = BorderStyle.solid
            borderWidth = 4.px
        }

        if (props.userName != null) {
            styledDiv {
                css { float = Float.right }

                button {
                    attrs.onClickFunction = { doLogout() }
                    +"Logout (${props.userName})"
                }
            }
        }

        h1 {
            routeLink("/") { +"Home" }
        }
    }
}

val NavBarConnector =
    rConnect<StoreState, RAction, WrapperAction, RProps, NavBarStateProps, NavBarDispatchProps, NavBarProps>(
        { state, _ ->
            userName = state.loggedUser
        },
        { dispatch, _ ->
            onLogout = { dispatch(LogoutStoreAction) }
        }
    )

val ConnectedNavBar = NavBarConnector(NavBar)

fun RBuilder.navBar(): ReactElement = ConnectedNavBar {}
