package component.main

import com.ccfraser.muirwik.components.*
import react.RBuilder
import react.RHandler
import react.RProps
import react.rFunction

interface MainPageProps : RProps {
    var title: String
    var backRoute: String?
}

val MainPage = rFunction("MainPage") { props: MainPageProps ->
    mGridContainer {
        attrs {
            spacing = MGridSpacing.spacing2
        }

        mGridItem(lg = MGridSize.cells12) {
            navBar(props.title, props.backRoute)
        }
        mGridItem(lg = MGridSize.cells12) {
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
