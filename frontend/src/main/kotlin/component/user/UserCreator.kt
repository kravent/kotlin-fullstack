package component.user

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RProps
import react.dom.button
import react.dom.div
import react.dom.input
import react.rFunction
import react.useState
import utils.withTarget

data class UserCreatorProps(
    val disabled: Boolean,
    val onCreateUserFunction: (userName: String) -> Unit
) : RProps

val UserCreator = rFunction("UserCreator") { props: UserCreatorProps ->
    val (userName, setUserName) = useState("")

    div {
        input(InputType.text) {
            attrs.disabled = props.disabled
            attrs.value = userName
            attrs.onChangeFunction = withTarget { userNameInput: HTMLInputElement ->
                setUserName(userNameInput.value)
            }
        }
        button {
            attrs.disabled = props.disabled
            attrs.onClickFunction = {
                if (userName.isNotEmpty()) {
                    props.onCreateUserFunction(userName)
                }
            }
            +"Add"
        }
    }
}

fun RBuilder.userCreator(
    disabled: Boolean,
    onCreateUserFunction: (userName: String) -> Unit
) = UserCreator.node(UserCreatorProps(disabled, onCreateUserFunction))
