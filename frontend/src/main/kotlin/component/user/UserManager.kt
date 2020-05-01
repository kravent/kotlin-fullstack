package component.user

import ajax.Api
import component.materialui.AlertSeverity
import component.materialui.alert
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.circularprogress.circularProgress
import materialui.components.grid.grid
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute
import react.*

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

val UserManager = rFunction("UserManager") { _: RProps ->
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

    useEffectWithCleanup(listOf()) {
        val job = reloadUsers()
        return@useEffectWithCleanup { job.cancel() }
    }

    grid {
        attrs {
            container = true
            spacing(4)
        }

        state.users?.also {
            grid {
                attrs {
                    item = true
                    lg(12)
                }
                userList(it)
            }
        }
        state.error?.also { errorText ->
            grid {
                attrs {
                    item = true
                    lg(12)
                }
                alert {
                    attrs.severity = AlertSeverity.error
                    +errorText
                }
            }
        }
        grid {
            attrs {
                item = true
                lg(6)
            }
            if (state.users != null) {
                userCreator(
                    disabled = state.loading,
                    onCreateUserFunction = { addUser(it) }
                )
            } else if (!state.loading) {
                button {
                    attrs.onClickFunction = { reloadUsers() }
                }
                +"Reload"
            }
        }
        if (state.loading) {
            grid {
                attrs {
                    item = true
                    lg(1)
                }
                circularProgress {}
            }
        }
    }
}

fun RBuilder.userManager() = UserManager {}
