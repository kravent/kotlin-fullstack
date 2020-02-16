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

interface NavBarComponentProps : RProps {
    var userName: String?
    var onLogin: (String) -> Unit
    var onLogout: () -> Unit
}

class NavBarComponent : RComponent<NavBarComponentProps, RState>() {
    override fun RBuilder.render() {
        styledDiv {
            css {
                borderBottomStyle = BorderStyle.solid
                borderWidth = 4.px
                textAlign = TextAlign.right
            }

            div {
                props.userName.let { userName ->
                    if (userName == null) {
                        button {
                            attrs.onClickFunction = { props.onLogin("user1") }
                            +"Login"
                        }
                    } else {
                        button {
                            attrs.onClickFunction = { props.onLogout() }
                            +"Logout ($userName)"
                        }
                    }
                }
            }
        }
    }

}

private val navBarLink =
    rConnect<StoreState, RAction, WrapperAction, RProps, NavBarComponentProps, NavBarComponentProps, NavBarComponentProps>(
        { state, _ ->
            userName = state.loggedUser
        },
        { dispatch, _ ->
            onLogin = { userName -> dispatch(LoginStoreAction(userName)) }
            onLogout = { dispatch(LogoutStoreAction) }
        }
    )(NavBarComponent::class.rClass)

fun RBuilder.navBar() = navBarLink {}
