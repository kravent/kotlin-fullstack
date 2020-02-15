package component.user

import ajax.Api
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.agaman.kotlinfullstack.model.UsersResponse
import react.*
import react.dom.div

sealed class UserManagerData;
object UserManagerDataLoading : UserManagerData()
data class UserManagerDataError(val error: String) : UserManagerData()
data class UserManagerDataSuccess(val users: List<String>) : UserManagerData()


interface UserManagerState : RState {
    var data: UserManagerData
}

class UserManagerComponent : RComponent<RProps, UserManagerState>() {
    override fun UserManagerState.init() {
        data = UserManagerDataLoading

        MainScope().launch {
            reloadUsers()
        }
    }

    private suspend fun reloadUsers() {
        try {
            val usersResponse = Api.get<UsersResponse>("/api/users")
            setState {
                data = UserManagerDataSuccess(usersResponse.users)
            }
        } catch (e: Exception) {
            setState {
                data = UserManagerDataError(e.message.orEmpty())
            }
        }
    }

    private fun addUser(userName: String) {
        TODO()
    }

    override fun RBuilder.render() {
        when(val data = state.data) {
            is UserManagerDataLoading -> div { +"Loading..." }
            is UserManagerDataError -> div { +"Error loading users: ${data.error}" }
            is UserManagerDataSuccess -> {
                userList(data.users)
                userCreator(onCreateUserFunction = { addUser(it) })
            }
        }
    }

}

fun RBuilder.userManager() = child(UserManagerComponent::class) {}
