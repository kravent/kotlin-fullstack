import component.App
import kotlinx.browser.document
import react.create
import react.dom.render

fun main() {
    render(
        element = App.create(),
        container = document.getElementById("root")!!,
    )
}
