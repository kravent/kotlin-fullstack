
import component.App
import kotlinx.browser.document
import react.create
import react.dom.client.createRoot

fun main() {
    val container = document.getElementById("root") ?: error("Root element not found")
    createRoot(container).render(App.create())
}
