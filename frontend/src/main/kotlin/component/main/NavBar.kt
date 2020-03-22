package component.main

import ajax.Api
import component.materialui.makeStyles
import component.store.LogoutStoreAction
import component.store.StoreState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.flexGrow
import kotlinx.html.js.onClickFunction
import materialui.components.appbar.appBar
import materialui.components.appbar.enums.AppBarPosition
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import react.*
import react.redux.rConnect
import react.router.dom.LinkComponent
import redux.RAction
import redux.WrapperAction

interface ConnectedNavBarProps : RProps {
    var title: String
    var backRoute: String?
}

interface NavBarStateProps : RProps {
    var userName: String?
}

interface NavBarDispatchProps : RProps {
    var onLogout: () -> Unit
}

data class NavBarProps(
    val title: String,
    val backRoute: String?,
    val userName: String?,
    val onLogout: () -> Unit
) : RProps

private val useStyles = makeStyles {
    "title" {
        flexGrow = 1.0
    }
}

val NavBar = rFunction("NavBarComponent") { props: NavBarProps ->
    val classes = useStyles()

    fun doLogout() {
        MainScope().launch {
            Api.logout()
            props.onLogout()
        }
    }

    appBar {
        attrs.position = AppBarPosition.static
        toolbar {
            if (props.backRoute != null) {
                iconButton {
                    attrs {
                        attrs.asDynamic().component = LinkComponent::class.js
                        attrs.asDynamic().to = props.backRoute
                    }
                    icon { +"arrow_back" }
                }
            }
            typography {
                attrs {
                    className = classes.title
                    variant = TypographyVariant.h6
                }
                +props.title
            }
            button {
                attrs {
                    color = ButtonColor.inherit
                    onClickFunction = { doLogout() }
                }
                +"Logout (${props.userName})"
            }
        }
    }
}

val NavBarConnector =
    rConnect<StoreState, RAction, WrapperAction, ConnectedNavBarProps, NavBarStateProps, NavBarDispatchProps, NavBarProps>(
        { state, _ ->
            userName = state.loggedUser
        },
        { dispatch, _ ->
            onLogout = { dispatch(LogoutStoreAction) }
        }
    )

val ConnectedNavBar = NavBarConnector(NavBar)

fun RBuilder.navBar(
    title: String,
    backRoute: String? = null
): ReactElement = ConnectedNavBar {
    attrs.title = title
    attrs.backRoute = backRoute
}
