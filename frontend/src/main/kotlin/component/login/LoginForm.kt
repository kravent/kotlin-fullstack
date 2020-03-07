package component.login

import ajax.Api
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.form.MFormControlVariant
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.*
import styled.css
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
    }

    override fun RBuilder.render() {
        mGridContainer {
            attrs {
                justify = MGridJustify.center
                alignItems = MGridAlignItems.center
            }

            mGridItem(xs = MGridSize.cells4) {
                mPaper {
                    css {
                        padding(all = 50.px)
                    }

                    mGridContainer {
                        attrs.direction = MGridDirection.column

                        mGridItem {
                            mTextField(
                                label = "User",
                                fullWidth = true,
                                variant = MFormControlVariant.outlined,
                                value = state.user,
                                disabled = state.loading,
                                error = state.error != null,
                                onChange = withTarget<HTMLInputElement> { setState { user = it.value } }
                            ) {
                                attrs.onKeyPress = { if (it.key == "Enter") doLogin() }
                            }
                        }

                        mGridItem {
                            mTextField(
                                label = "Password",
                                fullWidth = true,
                                variant = MFormControlVariant.outlined,
                                type = InputType.password,
                                value = state.password,
                                disabled = state.loading,
                                error = state.error != null,
                                helperText = state.error,
                                onChange = withTarget<HTMLInputElement> { setState { password = it.value } }
                            ) {
                                attrs.onKeyPress = { if (it.key == "Enter") doLogin() }
                            }
                        }

                        mGridItem {
                            styledDiv {
                                css {
                                    marginTop = 16.px
                                    marginBottom = 8.px
                                    textAlign = TextAlign.right
                                }
                                mButton(
                                    caption = "Login",
                                    variant = MButtonVariant.contained,
                                    color = MColor.primary,
                                    onClick = { doLogin() }
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

fun RBuilder.loginForm(onUserLogged: (userName: String) -> Unit) = child(LoginForm::class) {
    attrs.onUserLogged = onUserLogged
}
