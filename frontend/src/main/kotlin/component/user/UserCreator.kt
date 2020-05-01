package component.user

import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonSize
import materialui.components.button.enums.ButtonVariant
import materialui.components.formcontrol.enums.FormControlMargin
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.grid.enums.GridAlignItems
import materialui.components.grid.enums.GridDirection
import materialui.components.grid.grid
import materialui.components.icon.icon
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import react.RBuilder
import react.RProps
import react.rFunction
import react.state
import utils.withEvent
import utils.withTarget

data class UserCreatorProps(
    val disabled: Boolean,
    val onCreateUserFunction: (userName: String) -> Unit
) : RProps

val UserCreator = rFunction("UserCreator") { props: UserCreatorProps ->
    var userName by state("")

    fun sendUser() {
        if (userName.isNotEmpty()) {
            props.onCreateUserFunction(userName)
        }
    }

    grid {
        attrs {
            container = true
            direction = GridDirection.row
            alignItems = GridAlignItems.center
            spacing(8)
        }

        grid {
            attrs.item = true

            textField {
                attrs {
                    onChangeFunction = withTarget<HTMLInputElement>{ userName = it.value }
                    onKeyPressFunction = withEvent<KeyboardEvent>{ if (it.key == "Enter") sendUser() }
                    variant = FormControlVariant.filled
                    margin = FormControlMargin.none
                    disabled = props.disabled
                    label { +"New user" }
                    value = userName
                }
            }
        }
        grid {
            attrs.item = true

            button {
                attrs {
                    variant = ButtonVariant.contained
                    size = ButtonSize.large
                    disabled = props.disabled
                    color = ButtonColor.secondary
                    onClickFunction = { sendUser() }
                }
                icon { +"add" }
                +"Add"
            }
        }
    }
}

fun RBuilder.userCreator(
    disabled: Boolean,
    onCreateUserFunction: (userName: String) -> Unit
) = child(UserCreator, UserCreatorProps(disabled, onCreateUserFunction)) {}
