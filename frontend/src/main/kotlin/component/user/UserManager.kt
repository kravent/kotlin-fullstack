package component.user

import react.*

interface UserManagerState : RState {
    var userList: MutableList<String>
}

class UserManagerComponent : RComponent<RProps, UserManagerState>() {
    override fun UserManagerState.init() {
        userList = mutableListOf()
    }

    private fun addUser(userName: String) {
        if (!state.userList.contains(userName)) {
            setState {
                userList.add(userName)
            }
        }
    }

    override fun RBuilder.render() {
        userList(state.userList)
        userCreator(onCreateUserFunction = { addUser(it) })
    }

}

fun RBuilder.userManager() = child(UserManagerComponent::class) {}
