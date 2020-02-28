package component.user

import ajax.Api
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onClickFunction
import me.agaman.kotlinfullstack.model.UserCreateRequest
import me.agaman.kotlinfullstack.model.UserCreateResponse
import me.agaman.kotlinfullstack.model.UserListResponse
import me.agaman.kotlinfullstack.route.ApiRoute
import react.*
import react.dom.button
import react.dom.div

sealed class UserManagerData
object UserManagerDataLoading : UserManagerData()
data class UserManagerDataError(val error: String) : UserManagerData()
data class UserManagerDataSuccess(val users: List<String>) : UserManagerData()

val UserManager = rFunction("UserManager") { _: RProps ->
    val (state, setState) = useState<UserManagerData>(UserManagerDataLoading)

    val reloadUsers = {
        setState(UserManagerDataLoading)
        MainScope().launch {
            setState(
                try {
                    val response = Api.get<UserListResponse>(ApiRoute.USERS_LIST)
                    UserManagerDataSuccess(response.users)
                } catch (e: Exception) {
                    UserManagerDataError("Error loading users")
                }
            )
        }
    }

    val addUser = { userName: String ->
        setState(UserManagerDataLoading)
        MainScope().launch {
            setState(
                try {
                    val response = Api.post<UserCreateResponse>(ApiRoute.USER_CREATE, UserCreateRequest(userName))
                    when(val error = response.error) {
                        null -> UserManagerDataSuccess(response.users.users)
                        else -> UserManagerDataError(error)
                    }
                } catch (e: Exception) {
                    UserManagerDataError("Error creating user")
                }
            )
        }
    }

    useEffect(listOf()) {
        reloadUsers()
    }

    when(state) {
        is UserManagerDataLoading -> div { +"Loading..." }
        is UserManagerDataError -> div {
            div { +state.error }
            button {
                attrs.onClickFunction = { reloadUsers() }
                +"Reload"
            }
        }
        is UserManagerDataSuccess -> {
            userList(state.users)
            userCreator(onCreateUserFunction = { addUser(it) })
        }
    }
}

fun RBuilder.userManager() = UserManager {}
