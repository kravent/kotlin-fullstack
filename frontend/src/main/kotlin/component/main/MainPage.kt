package component.main

import materialui.components.grid.grid
import react.RBuilder
import react.RHandler
import react.RProps
import react.rFunction

interface MainPageProps : RProps {
    var title: String
    var backRoute: String?
}

val MainPage = rFunction("MainPage") { props: MainPageProps ->
    grid {
        attrs {
            container = true
            spacing(2)
        }

        grid {
            attrs {
                item = true
                lg(12)
            }
            navBar(props.title, props.backRoute)
        }
        grid {
            attrs {
                item = true
                lg(12)
            }
            props.children()
        }
    }
}

fun RBuilder.mainPage(
    title: String,
    backRoute: String? = null,
    handler: RHandler<MainPageProps>
) = MainPage {
    attrs.title = title
    attrs.backRoute = backRoute
    handler()
}
