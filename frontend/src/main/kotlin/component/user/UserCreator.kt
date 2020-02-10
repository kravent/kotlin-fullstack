package component.user

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.button
import react.dom.div
import react.dom.input
import utils.withTarget

interface UserCreatorProps : RProps {
    var onCreateUserFunction: (userName: String) -> Unit
}

interface UserCreatorState : RState {
    var userName: String
}

class UserCreatorComponent : RComponent<UserCreatorProps, UserCreatorState>() {
    override fun UserCreatorState.init() {
        userName = ""
    }

    private fun onUserNameChange(userNameInput: HTMLInputElement) {
        setState {
            userName = userNameInput.value
        }
    }

    private fun onAddName(event: Event) {
        if (state.userName.isNotEmpty()) {
            props.onCreateUserFunction(state.userName)
        }
    }

    override fun RBuilder.render() {
        div {
            input(InputType.text) {
                attrs.value = state.userName
                attrs.onChangeFunction = withTarget(::onUserNameChange)
            }
            button {
                attrs.onClickFunction = ::onAddName
                +"Add"
            }
        }
    }
}

fun RBuilder.userCreator(onCreateUserFunction: (userName: String) -> Unit) = child(UserCreatorComponent::class) {
    attrs.onCreateUserFunction = onCreateUserFunction
}
