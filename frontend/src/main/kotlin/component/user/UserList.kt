package component.user

import materialui.components.icon.icon
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.lab.components.alert.alert
import materialui.lab.components.alert.enums.AlertSeverity
import react.RBuilder
import react.RProps
import react.rFunction

data class UserListProps(
    val userList: List<String>
) : RProps

val UserList = rFunction("UserList") { props: UserListProps ->
    if (props.userList.isEmpty()) {
        alert {
            attrs.severity = AlertSeverity.info
            +"No users found, you can create the first one below"
        }
    } else {
        list {
            props.userList.forEach { userName ->
                listItem {
                    listItemIcon {
                        icon { +"person" }
                    }
                    listItemText { +userName }
                }
            }
        }
    }
}

fun RBuilder.userList(userList: List<String>) = UserList.node(UserListProps(userList))
