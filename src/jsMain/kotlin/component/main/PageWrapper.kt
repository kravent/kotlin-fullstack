package component.main

import mui.material.Grid
import mui.system.responsive
import react.PropsWithChildren
import utils.NC

external interface MainPageProps : PropsWithChildren {
    var title: String
    var backRoute: String?
}

val PageWrapper by NC { props: MainPageProps ->
    Grid {
        container = true
        spacing = responsive(2)

        Grid {
            item = true
            this.asDynamic().lg = 12
            NavBar {
                title = props.title
                backRoute = props.backRoute
            }
        }
        Grid {
            item = true
            this.asDynamic().lg = 12
            +props.children
        }
    }
}
