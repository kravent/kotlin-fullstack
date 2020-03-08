package component.login

import ajax.Api
import ajax.ApiForbiddenException
import ajax.ApiUnauthoridedException
import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.form.MFormControlVariant
import component.materialui.MAlertSeverity
import component.materialui.mAlert
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.InputType
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RProps
import react.rFunction
import react.useState
import styled.css
import utils.withTarget

data class LoginPageProps(
    val onUserLogged: (userName: String) -> Unit
) : RProps

val LoginPage = rFunction("LoginPage") { props: LoginPageProps ->
    val (loading, setLoading) = useState(false)
    val (user, setUser) = useState("")
    val (password, setPassword) = useState("")
    val (error, setError) = useState(null as String?)

    fun doLogin() {
        setLoading(true)
        MainScope().launch {
            try {
                Api.login(user, password)
                props.onUserLogged(user)
                setUser("")
                setPassword("")
                setError(null)
            } catch (e: ApiUnauthoridedException) {
                setError("User not found")
            } catch (e: ApiForbiddenException) {
                setError("CSRF token check error")
            } catch (e: Exception) {
                setError("Ooops! Something went wrong")
            } finally {
                setLoading(false)
            }
        }
    }

    mGridContainer {
        attrs {
            justify = MGridJustify.center
            alignItems = MGridAlignItems.center
        }

        mGridItem(xs = MGridSize.cells4) {
            mPaper {
                css {
                    marginTop = 50.px
                    padding(all = 50.px)
                }

                mGridContainer {
                    attrs {
                        direction = MGridDirection.column
                        spacing = MGridSpacing.spacing6
                    }

                    if (error != null) {
                        mGridItem {
                            mAlert(text = error, severity = MAlertSeverity.error)
                        }
                    }

                    mGridItem {
                        mTextField(
                            label = "User",
                            fullWidth = true,
                            margin = MFormControlMargin.none,
                            variant = MFormControlVariant.outlined,
                            value = user,
                            disabled = loading,
                            error = error != null,
                            onChange = withTarget<HTMLInputElement> { setUser(it.value) }
                        ) {
                            attrs.onKeyPress = { if (it.key == "Enter") doLogin() }
                        }
                    }

                    mGridItem {
                        mTextField(
                            label = "Password",
                            fullWidth = true,
                            margin = MFormControlMargin.none,
                            variant = MFormControlVariant.outlined,
                            type = InputType.password,
                            value = password,
                            disabled = loading,
                            error = error != null,
                            onChange = withTarget<HTMLInputElement> { setPassword(it.value) }
                        ) {
                            attrs.onKeyPress = { if (it.key == "Enter") doLogin() }
                        }
                    }

                    mGridItem {
                        css {
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

fun RBuilder.loginPage(onUserLogged: (userName: String) -> Unit) = child(LoginPage, LoginPageProps(onUserLogged)) {}
