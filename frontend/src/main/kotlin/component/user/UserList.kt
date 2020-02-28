package component.user

import react.RBuilder
import react.RProps
import react.dom.li
import react.dom.ul
import react.rFunction

data class UserListProps(
    val userList: List<String>
) : RProps

val UserList = rFunction("UserList") { props: UserListProps ->
    ul {
        props.userList.forEach {
            li { +it }
        }
    }
}

fun RBuilder.userList(userList: List<String>) = UserList.node(UserListProps(userList))
