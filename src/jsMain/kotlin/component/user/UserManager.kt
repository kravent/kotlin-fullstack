package component.user

import ajax.Api
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute
import mui.material.*
import mui.system.ResponsiveStyleValue
import react.FC
import react.Props
import react.useEffectOnce
import react.useReducer

private data class UserManagerState(
    val loading: Boolean = true,
    val error: String? = null,
    val users: List<String>? = null
)

private sealed class UserManagerEvent {
    object Loading : UserManagerEvent()
    data class Error(val error: String) : UserManagerEvent()
    data class UsersList(val response: UserListResponse) : UserManagerEvent()
    data class UserCreate(val response: UserCreateResponse) : UserManagerEvent()
}

private fun stateReducer(state: UserManagerState, event: UserManagerEvent): UserManagerState = when (event) {
    is UserManagerEvent.Loading -> state.copy(loading = true, error = null)
    is UserManagerEvent.UsersList -> state.copy(loading = false, error = null, users = event.response.users)
    is UserManagerEvent.UserCreate -> state.copy(loading = false, error = event.response.error, users = event.response.users.users)
    is UserManagerEvent.Error -> state.copy(loading = false, error = event.error)
}

val UserManager = FC<Props>("UserManager") {
    val (state, onStateEvent) = useReducer(::stateReducer, UserManagerState())

    fun reloadUsers(): Job {
        onStateEvent(UserManagerEvent.Loading)
        return MainScope().launch {
            try {
                val response = Api.get<UserListResponse>(ApiRoute.USERS_LIST)
                delay(2000) // TODO Remove artificial delay added to check loading page
                onStateEvent(UserManagerEvent.UsersList(response))
            } catch (e: Exception) {
                onStateEvent(UserManagerEvent.Error("Error loading users"))
            }
        }
    }

    fun addUser(userName: String): Job {
        onStateEvent(UserManagerEvent.Loading)
        return MainScope().launch {
            try {
                val response = Api.post<UserCreateResponse>(ApiRoute.USER_CREATE, UserCreateRequest(userName))
                delay(2000) // TODO Remove artificial delay added to check loading page
                onStateEvent(UserManagerEvent.UserCreate(response))
            } catch (e: Exception) {
                onStateEvent(UserManagerEvent.Error("Error creating user"))
            }
        }
    }

    useEffectOnce {
        val job = reloadUsers()
        cleanup {
            job.cancel()
        }
    }

    Grid {
        container = true
        spacing = ResponsiveStyleValue(4)

        state.users?.also {
            Grid {
                item = true
                lg = 12
                UserList {
                    userList = it
                }
            }
        }
        state.error?.also { error ->
            Grid {
                item = true
                lg = 6
                Alert {
                    severity = AlertColor.error
                    +error
                }
            }
        }
        Grid {
            item = true
            lg = 6
            if (state.users != null) {
                UserCreator {
                    disabled = state.loading
                    onCreateUserFunction = { addUser(it) }
                }
            } else if (!state.loading) {
                Button {
                    onClick = { reloadUsers() }
                    +"Reload"
                }
            }
        }
        if (state.loading) {
            Grid {
                item = true
                lg = 12
                CircularProgress {}
            }
        }
    }
}
