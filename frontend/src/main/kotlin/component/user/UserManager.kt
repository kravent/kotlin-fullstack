package component.user

import ajax.Api
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.css.Color
import kotlinx.css.color
import kotlinx.html.js.onClickFunction
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute
import react.*
import react.dom.button
import styled.css
import styled.styledDiv

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

    fun reloadUsers() {
        onStateEvent(UserManagerEvent.Loading)
        MainScope().launch {
            try {
                val response = Api.get<UserListResponse>(ApiRoute.USERS_LIST)
                onStateEvent(UserManagerEvent.UsersList(response))
            } catch (e: Exception) {
                onStateEvent(UserManagerEvent.Error("Error loading users"))
            }
        }
    }

    fun addUser(userName: String) {
        onStateEvent(UserManagerEvent.Loading)
        MainScope().launch {
            try {
                val response = Api.post<UserCreateResponse>(ApiRoute.USER_CREATE, UserCreateRequest(userName))
                onStateEvent(UserManagerEvent.UserCreate(response))
            } catch (e: Exception) {
                onStateEvent(UserManagerEvent.Error("Error creating user"))
            }
        }
    }

    useEffect(listOf()) {
        reloadUsers()
    }

    if (state.loading) {
        styledDiv {
            css { color = Color.indigo }
            +"Loading..."
        }
    }
    state.users?.also { userList(it) }
    state.error?.also {
        styledDiv {
            css { color = Color.red }
            +it
        }
    }
    if (state.users != null) {
        userCreator(
            disabled = state.loading,
            onCreateUserFunction = { addUser(it) }
        )
    } else if (!state.loading) {
        button {
            attrs.onClickFunction = { reloadUsers() }
            +"Reload"
        }
    }
}

fun RBuilder.userManager() = UserManager {}
