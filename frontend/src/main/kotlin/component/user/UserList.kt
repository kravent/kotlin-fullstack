package component.user

import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.li
import react.dom.ul

interface UserListProps : RProps {
    var userList: List<String>
}

class UserListComponent : RComponent<UserListProps, RState>() {
    override fun RBuilder.render() {
        ul {
            props.userList.forEach {
                li { +it }
            }
        }
    }
}

fun RBuilder.userList(userList: List<String>) = child(UserListComponent::class) {
    attrs.userList = userList
}
