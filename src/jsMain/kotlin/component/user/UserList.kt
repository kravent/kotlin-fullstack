package component.user

import mui.icons.material.Person
import mui.material.*
import react.FC
import react.Props

external interface UserListProps : Props {
    var userList: List<String>
}

val UserList = FC("UserList") { props: UserListProps ->
    if (props.userList.isEmpty()) {
        Alert {
            severity = AlertColor.info
            +"No users found, you can create the first one below"
        }
    } else {
        List {
            props.userList.forEach { user ->
                ListItem {
                    ListItemIcon { Person() }
                    ListItemText { +user }
                }
            }
        }
    }
}
