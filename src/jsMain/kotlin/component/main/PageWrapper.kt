package component.main

import mui.material.Grid
import mui.system.ResponsiveStyleValue
import react.PropsWithChildren
import utils.NC

external interface MainPageProps : PropsWithChildren {
    var title: String
    var backRoute: String?
}

val PageWrapper by NC { props: MainPageProps ->
    Grid {
        container = true
        spacing = ResponsiveStyleValue(2)

        Grid {
            item = true
            lg = 12
            NavBar {
                title = props.title
                backRoute = props.backRoute
            }
        }
        Grid {
            item = true
            lg = 12
            props.children()
        }
    }
}
