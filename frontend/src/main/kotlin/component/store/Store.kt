package component.store

import kotlinx.serialization.Serializable
import me.agaman.kotlinfullstack.Serializer
import org.w3c.dom.get
import org.w3c.dom.set
import react.RBuilder
import react.RHandler
import react.redux.ProviderProps
import react.redux.provider
import redux.RAction
import redux.createStore
import redux.rEnhancer
import kotlin.browser.localStorage

private const val LOCAL_STORAGE_KEY = "storeState"

@Serializable
data class StoreState(
    val loggedUser: String? = null
)

class LoginStoreAction(val userName: String) : RAction
object LogoutStoreAction : RAction

private fun storeReducer(previousState: StoreState, action: RAction) = when (action) {
    is LoginStoreAction -> previousState.copy(loggedUser = action.userName)
    is LogoutStoreAction -> previousState.copy(loggedUser = null)
    else -> previousState
}


private fun loadState(): StoreState =
    localStorage[LOCAL_STORAGE_KEY]
        ?.let { Serializer.json.parse(StoreState.serializer(), it) }
        ?: StoreState()

private fun saveState(state: StoreState) {
    localStorage[LOCAL_STORAGE_KEY] = Serializer.json.stringify(StoreState.serializer(), state)
}

val Store = createStore<StoreState, RAction, dynamic>(
    ::storeReducer,
    loadState(),
    rEnhancer()
).also {
    it.subscribe { saveState(it.getState()) }
}

fun RBuilder.storeProvider(handler: RHandler<ProviderProps>) = provider(Store) {
    handler()
}
