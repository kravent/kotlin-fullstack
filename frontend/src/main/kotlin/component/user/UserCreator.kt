package component.user

import com.ccfraser.muirwik.components.*
import com.ccfraser.muirwik.components.button.MButtonSize
import com.ccfraser.muirwik.components.button.MButtonVariant
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.form.MFormControlMargin
import com.ccfraser.muirwik.components.form.MFormControlVariant
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RProps
import react.rFunction
import react.useState
import utils.withTarget

data class UserCreatorProps(
    val disabled: Boolean,
    val onCreateUserFunction: (userName: String) -> Unit
) : RProps

val UserCreator = rFunction("UserCreator") { props: UserCreatorProps ->
    val (userName, setUserName) = useState("")

    fun sendUser() {
        if (userName.isNotEmpty()) {
            props.onCreateUserFunction(userName)
        }
    }

    mGridContainer {
        attrs.direction = MGridDirection.row
        attrs.alignItems = MGridAlignItems.center
        attrs.spacing = MGridSpacing.spacing8

        mGridItem {
            mTextField(
                label = "New user",
                variant = MFormControlVariant.filled,
                margin = MFormControlMargin.none,
                value = userName,
                disabled = props.disabled,
                onChange = withTarget<HTMLInputElement>{ setUserName(it.value) }
            ) {
                attrs.onKeyPress = { if (it.key == "Enter") sendUser() }
            }
        }
        mGridItem {
            mButton(
                caption = "Add",
                variant = MButtonVariant.contained,
                size = MButtonSize.large,
                disabled = props.disabled,
                color = MColor.secondary,
                onClick = { sendUser() }
            )
        }
    }
}

fun RBuilder.userCreator(
    disabled: Boolean,
    onCreateUserFunction: (userName: String) -> Unit
) = child(UserCreator, UserCreatorProps(disabled, onCreateUserFunction)) {}
