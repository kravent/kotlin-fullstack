package component.main

import ajax.Api
import component.store.LogoutStoreAction
import component.store.StoreState
import csstype.FlexGrow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mui.icons.material.ArrowBack
import mui.material.*
import react.Props
import react.createElement
import react.invoke
import react.redux.rConnect
import react.router.useNavigate
import redux.RAction
import redux.WrapperAction
import utils.NC

external interface NavBarProps : Props {
    var title: String
    var backRoute: String?
}

external interface NavBarStateProps : Props {
    var userName: String?
}

external interface NavBarDispatchProps : Props {
    var onLogout: () -> Unit
}

external interface InnerNavBarProps : NavBarProps, NavBarStateProps, NavBarDispatchProps

val InnerNavBar by NC { props: InnerNavBarProps ->
    val navigate = useNavigate()

    fun doLogout() {
        MainScope().launch {
            Api.logout()
            props.onLogout()
        }
    }

    AppBar {
        position = AppBarPosition.static

        Toolbar {
            props.backRoute?.let { backRoute ->
                IconButton {
                    color = IconButtonColor.inherit
                    onClick = { navigate(to = backRoute) }
                    ArrowBack()
                }
            }
            Typography {
                variant = "h6"
                sx = jso { flexGrow = FlexGrow(1.0) }
                +props.title
            }
            Button {
                color = ButtonColor.inherit
                onClick = { doLogout() }
                +"Logout (${props.userName})"
            }
        }
    }
}

val NavBarConnector =
    rConnect<StoreState, RAction, WrapperAction, NavBarProps, NavBarStateProps, NavBarDispatchProps, InnerNavBarProps>(
        { state, _ ->
            userName = state.loggedUser
        },
        { dispatch, _ ->
            onLogout = { dispatch(LogoutStoreAction) }
        }
    )

val NavBar = NavBarConnector { props ->
    child(createElement(InnerNavBar, props))
}
