package component.user

import mui.icons.material.Person
import mui.material.*
import react.Props
import utils.NC

external interface UserListProps : Props {
    var userList: List<String>
}

val UserList by NC { props: UserListProps ->
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
