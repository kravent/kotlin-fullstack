package component.user

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import me.agaman.kotlinfullstack.model.UsersResponse
import react.*
import react.dom.div
import kotlin.browser.window

interface UserManagerState : RState {
    var loading: Boolean
    var error: String?
    var userList: List<String>
}

class UserManagerComponent : RComponent<RProps, UserManagerState>() {
    override fun UserManagerState.init() {
        loading = true
        error = null
        userList = emptyList()

        MainScope().launch {
            reloadUsers()
        }
    }

    private suspend fun reloadUsers() {
        try {
            val usersResponse = window.fetch("/api/users").await().json().await().unsafeCast<UsersResponse>()
            setState {
                    loading = false
                    error = null
                    userList = usersResponse.users
                }
        } catch (e: Exception) {
            setState {
                loading = false
                error = e.message
                userList = emptyList()
            }
        }
    }

    private fun addUser(userName: String) {
        if (!state.userList.contains(userName)) {
            setState {
                userList += userName
            }
        }
    }

    override fun RBuilder.render() {
        when {
            state.loading -> div { +"Loading..." }
            state.error != null -> div { +"Error loading users: ${state.error}" }
            else -> {
                userList(state.userList)
                userCreator(onCreateUserFunction = { addUser(it) })
            }
        }
    }

}

fun RBuilder.userManager() = child(UserManagerComponent::class) {}
