package component.login

import ajax.Api
import ajax.ApiForbiddenException
import ajax.ApiUnauthoridedException
import csstype.JustifyContent
import csstype.JustifyItems
import csstype.TextAlign
import csstype.px
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.js.jso
import mui.material.*
import mui.system.responsive
import react.Props
import react.ReactNode
import react.dom.onChange
import react.useState
import utils.NC
import utils.handleInputEvent

external interface LoginPageProps : Props {
    var onUserLogged: (userName: String) -> Unit
}

val LoginPage by NC { props: LoginPageProps ->
    var loading by useState(false)
    var user by useState("")
    var password by useState("")
    var errorMessage by useState(null as String?)

    fun doLogin() {
        loading = true
        MainScope().launch {
            try {
                Api.login(user, password)
                user = ""
                password = ""
                errorMessage = null
                loading = false
                props.onUserLogged(user)
            } catch (e: ApiUnauthoridedException) {
                errorMessage = "User not found"
                loading = false
            } catch (e: ApiForbiddenException) {
                errorMessage = "CSRF token check error"
                loading = false
            } catch (e: Exception) {
                errorMessage = "Ooops! Something went wrong"
                loading = false
            }
        }
    }

    Grid {
        container = true
        sx = jso {
            justifyContent = JustifyContent.center
            justifyItems = JustifyItems.center
        }

        Grid {
            item = true
            this.asDynamic().xs = 4
            Paper {
                sx = jso {
                    marginTop = 50.px
                    padding = 50.px
                }

                Grid {
                    container = true
                    direction = responsive(GridDirection.column)
                    spacing = responsive(6)

                    errorMessage?.let {
                        Grid {
                            item = true
                            Alert {
                                severity = AlertColor.error
                                +it
                            }
                        }
                    }

                    Grid {
                        item = true
                        TextField {
                            label = ReactNode("User")
                            fullWidth = true
                            margin = FormControlMargin.none
                            variant = FormControlVariant.outlined
                            value = user
                            disabled = loading
                            error = errorMessage != null
                            onChange = handleInputEvent { user = it.value }
                            onKeyDown = { if (it.key == "Enter") doLogin() }
                        }
                    }

                    Grid {
                        item = true
                        TextField {
                            label = ReactNode("Password")
                            fullWidth = true
                            margin = FormControlMargin.none
                            variant = FormControlVariant.outlined
                            value = password
                            disabled = loading
                            error = errorMessage != null
                            onChange = handleInputEvent { password = it.value }
                            onKeyDown = { if (it.key == "Enter") doLogin() }
                        }
                    }

                    Grid {
                        item = true
                        sx = jso {
                            textAlign = TextAlign.right
                        }
                        Button {
                            variant = ButtonVariant.contained
                            color = ButtonColor.primary
                            onClick = { doLogin() }
                            +"Login"
                        }
                    }
                }
            }
        }
    }
}
