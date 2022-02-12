package component.user

import csstype.AlignItems
import kotlinx.js.jso
import mui.material.*
import mui.system.ResponsiveStyleValue
import react.FC
import react.Props
import react.ReactNode
import react.dom.onChange
import react.useState
import utils.handleInputEvent

external interface UserCreatorProps : Props {
    var disabled: Boolean
    var onCreateUserFunction: (userName: String) -> Unit
}

val UserCreator = FC("UserCreator") { props: UserCreatorProps ->
    val (userName, setUserName) = useState("")

    fun sendUser() {
        if (userName.isNotEmpty()) {
            props.onCreateUserFunction(userName)
        }
    }

    Grid {
        container = true
        direction = ResponsiveStyleValue(GridDirection.row)
        spacing = ResponsiveStyleValue(8)
        sx = jso { alignItems = AlignItems.center }

        Grid {
            item = true
            TextField {
                label = ReactNode("New user")
                variant = FormControlVariant.filled
                margin = FormControlMargin.none
                value = userName
                disabled = props.disabled
                onChange = handleInputEvent{ setUserName(it.value) }
                onKeyPress = { if(it.key == "Enter") sendUser() }
            }
        }
        Grid {
            item = true
            Button {
                variant = ButtonVariant.contained
                size = Size.large
                color = ButtonColor.secondary
                disabled = props.disabled
                onClick = { sendUser() }
                +"Add"
            }
        }
    }
}
