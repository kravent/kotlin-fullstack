package component

import component.store.LoginStoreAction
import component.store.LogoutStoreAction
import component.store.StoreState
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.button
import react.dom.div
import react.redux.rConnect
import redux.RAction
import redux.WrapperAction
import styled.css
import styled.styledDiv

interface NavBarStateProps : RProps {
    var userName: String?
}

interface NavBarDispatchProps : RProps {
    var onLogin: (String) -> Unit
    var onLogout: () -> Unit
}

data class NavBarProps(
    val userName: String?,
    val onLogin: (String) -> Unit,
    val onLogout: () -> Unit
) : RProps

val NavBar = rFunction("NavBarComponent") { props: NavBarProps ->
    styledDiv {
        css {
            borderBottomStyle = BorderStyle.solid
            borderWidth = 4.px
            textAlign = TextAlign.right
        }

        div {
            if (props.userName == null) {
                button {
                    attrs.onClickFunction = { props.onLogin("user1") }
                    +"Login"
                }
            } else {
                button {
                    attrs.onClickFunction = { props.onLogout() }
                    +"Logout (${props.userName})"
                }
            }
        }
    }
}

val NavBarConnector =
    rConnect<StoreState, RAction, WrapperAction, RProps, NavBarStateProps, NavBarDispatchProps, NavBarProps>(
        { state, _ ->
            userName = state.loggedUser
        },
        { dispatch, _ ->
            onLogin = { userName -> dispatch(LoginStoreAction(userName)) }
            onLogout = { dispatch(LogoutStoreAction) }
        }
    )

val ConnectedNavBar: RClass<RProps> = NavBarConnector(NavBar)

fun RBuilder.navBar(): ReactElement = ConnectedNavBar {}
