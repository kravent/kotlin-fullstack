package component.store

import react.RBuilder
import react.RHandler
import react.redux.ProviderProps
import react.redux.provider
import redux.RAction
import redux.createStore
import redux.rEnhancer

private val STORE = createStore<StoreState, RAction, dynamic>(::storeReducer, StoreState(), rEnhancer())

data class StoreState(
    val loggedUser: String? = null
)

fun storeReducer(previousState: StoreState, action: RAction) = when (action) {
    is LoginStoreAction -> previousState.copy(loggedUser = action.userName)
    is LogoutStoreAction -> previousState.copy(loggedUser = null)
    else -> previousState
}


class LoginStoreAction(val userName: String) : RAction
object LogoutStoreAction : RAction

fun RBuilder.storeProvider(handler: RHandler<ProviderProps>) = provider(STORE) {
    handler()
}
