package component.login

import ajax.Api
import ajax.ApiForbiddenException
import ajax.ApiUnauthoridedException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.*
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonVariant
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.grid.enums.GridAlignItems
import materialui.components.grid.enums.GridDirection
import materialui.components.grid.enums.GridJustify
import materialui.components.grid.grid
import materialui.components.paper.paper
import materialui.components.textfield.textField
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import materialui.styles.withStyles
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import react.RBuilder
import react.RProps
import react.rFunction
import react.state
import utils.withEvent
import utils.withTarget

data class LoginPageProps(
    val onUserLogged: (userName: String) -> Unit
) : RProps

val LoginPage = rFunction("LoginPage") { props: LoginPageProps ->
    val boxStyle = props.asDynamic()["classes"]["box"] as String
    val buttonContainerStyle = props.asDynamic()["classes"]["buttonContainer"] as String

    var loading by state(false)
    var user by state("")
    var password by state("")
    var errorMessage by state(null as String?)

    fun doLogin() {
        loading = true
        MainScope().launch {
            try {
                Api.login(user, password)
                props.onUserLogged(user)
                user = ""
                password = ""
                errorMessage = null
            } catch (e: ApiUnauthoridedException) {
                errorMessage = "User not found"
            } catch (e: ApiForbiddenException) {
                errorMessage = "CSRF token check error"
            } catch (e: Exception) {
                errorMessage = "Ooops! Something went wrong"
            } finally {
                loading = false
            }
        }
    }

    grid {
        attrs {
            container = true
            justify = GridJustify.center
            alignItems = GridAlignItems.center
        }

        grid {
            attrs {
                item = true
                xs(4)
            }

            paper {
                attrs.className = boxStyle

                grid {
                    attrs {
                        container = true
                        direction = GridDirection.column
                        spacing(6)
                    }

                    errorMessage?.let {
                        grid {
                            attrs.item = true

                            alert {
                                attrs.severity = AlertSeverity.error
                                +it
                            }
                        }
                    }

                    grid {
                        attrs.item = true

                        textField {
                            attrs {
                                label { +"User" }
                                fullWidth = true
                                margin = FormControlMargin.none
                                variant = FormControlVariant.outlined
                                disabled = loading
                                error = errorMessage != null
                                value = user
                                onChangeFunction = withTarget<HTMLInputElement> { user = it.value }
                                onKeyPressFunction = withEvent<KeyboardEvent>{ if (it.key == "Enter") doLogin() }
                            }
                        }
                    }

                    grid {
                        attrs.item = true

                        textField {
                            attrs {
                                label { +"Password" }
                                fullWidth = true
                                margin = FormControlMargin.none
                                variant = FormControlVariant.outlined
                                type = InputType.password
                                disabled = loading
                                error = errorMessage != null
                                value = password
                                onChangeFunction = withTarget<HTMLInputElement> { password = it.value }
                                onKeyPressFunction = withEvent<KeyboardEvent>{ if (it.key == "Enter") doLogin() }
                            }
                        }
                    }

                    grid {
                        attrs {
                            className = buttonContainerStyle
                            item = true
                        }

                        button {
                            attrs {
                                variant = ButtonVariant.contained
                                color = ButtonColor.primary
                                onClickFunction = { doLogin() }
                            }
                            +"Login"
                        }
                    }
                }
            }
        }
    }
}

val StyledLoginPage = withStyles(LoginPage, {
    "box" {
        marginTop = 50.px
        padding(all = 50.px)
    }
    "buttonContainer" {
        textAlign = TextAlign.right
    }
})

fun RBuilder.loginPage(onUserLogged: (userName: String) -> Unit) = child(StyledLoginPage, LoginPageProps(onUserLogged)) {}
