package component.main

import ajax.Api
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.button.mIconButton
import component.store.LogoutStoreAction
import component.store.StoreState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.flexGrow
import react.*
import react.redux.rConnect
import react.router.dom.LinkComponent
import redux.RAction
import redux.WrapperAction
import styled.css

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

val NavBar = rFunction("NavBarComponent") { props: NavBarProps ->
    fun doLogout() {
        MainScope().launch {
            Api.logout()
            props.onLogout()
        }
    }

    mAppBar(position = MAppBarPosition.static) {
        mToolbar {
            if (props.backRoute != null) {
                mIconButton(iconName = "arrow_back", color = MColor.inherit) {
                    attrs.asDynamic().component = LinkComponent::class.js
                    attrs.asDynamic().to = props.backRoute
                }
            }
            mTypography(variant = MTypographyVariant.h6) {
                css { flexGrow = 1.0 }
                +props.title
            }
            mButton(caption = "Logout (${props.userName})", color = MColor.inherit, onClick = { doLogout() })
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
