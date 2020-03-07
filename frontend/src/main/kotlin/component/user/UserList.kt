package component.user

import com.ccfraser.muirwik.components.list.mList
import com.ccfraser.muirwik.components.list.mListItemWithIcon
import react.RBuilder
import react.RProps
import react.rFunction

data class UserListProps(
    val userList: List<String>
) : RProps

val UserList = rFunction("UserList") { props: UserListProps ->
    mList {
        props.userList.forEach {
            mListItemWithIcon(iconName = "person", primaryText = it)
        }
    }
}

fun RBuilder.userList(userList: List<String>) = UserList.node(UserListProps(userList))
