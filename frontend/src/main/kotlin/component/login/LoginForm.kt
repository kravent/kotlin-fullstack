package component.login

import ajax.Api
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onSubmitFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.div
import react.dom.form
import react.dom.input
import styled.css
import styled.styledButton
import styled.styledDiv
import utils.withTarget

interface LoginFormProps : RProps {
    var onUserLogged: (userName: String) -> Unit
}

interface LoginFormState : RState {
    var loading: Boolean
    var user: String
    var password: String
    var error: String?
}

class LoginForm : RComponent<LoginFormProps, LoginFormState>() {
    override fun LoginFormState.init() {
        loading = false
        user = ""
        password = ""
        error = null
    }

    private fun doLogin() {
        setState { loading = true }
        MainScope().launch {
            try {
                Api.login(state.user, state.password)
                props.onUserLogged(state.user)
                setState {
                    loading = false
                    user = ""
                    password = ""
                    error = null
                }
            } catch (e: Exception) {
                setState {
                    loading = false
                    error = "User not found"
                }
            }
        }
        if (state.password == "pass") {

        } else {
        }
    }

    override fun RBuilder.render() {
        form {
            attrs.onSubmitFunction = {
                it.preventDefault()
                doLogin()
            }

            styledDiv {
                css { textAlign = TextAlign.center }
                styledDiv {
                    css { textAlign = TextAlign.left }
                    state.error?.let {
                        styledDiv {
                            css { color = Color.red }
                            +it
                        }
                    }
                    div {
                        +"Name:"
                        input {
                            attrs.disabled = state.loading
                            attrs.value = state.user
                            attrs.onChangeFunction = withTarget<HTMLInputElement> {
                                setState { user = it.value }
                            }
                        }
                    }
                    div {
                        +"Password:"
                        input {
                            attrs.disabled = state.loading
                            attrs.type = InputType.password
                            attrs.value = state.password
                            attrs.onChangeFunction = withTarget<HTMLInputElement> {
                                setState { password = it.value }
                            }
                        }
                    }
                    styledButton {
                        css { float = Float.left }
                        attrs.disabled = state.loading
                        attrs.type = ButtonType.submit
                        +"Login"
                    }
                }
            }
        }
    }

}

fun RBuilder.loginForm(onUserLogged: (userName: String) -> Unit) = child(LoginForm::class) {
    attrs.onUserLogged = onUserLogged
}
