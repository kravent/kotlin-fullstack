package component.user

import ajax.Api
import com.ccfraser.muirwik.components.MTypographyColor
import com.ccfraser.muirwik.components.button.mButton
import com.ccfraser.muirwik.components.mCircularProgress
import com.ccfraser.muirwik.components.mTypography
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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

    if (state.loading) {
        mCircularProgress()
    }
    state.users?.also { userList(it) }
    state.error?.also {
        mTypography(color = MTypographyColor.error) { +it }
    }
    if (state.users != null) {
        userCreator(
            disabled = state.loading,
            onCreateUserFunction = { addUser(it) }
        )
    } else if (!state.loading) {
        mButton(
            caption = "Reload",
            onClick = { reloadUsers() }
        )
    }
}

fun RBuilder.userManager() = UserManager {}
