package component

import component.main.PageWrapper
import component.user.UserManager
import react.FC
import react.Props
import react.createElement
import react.dom.html.ReactHTML.h1
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import react.router.dom.Link as RouterLink

val IndexPage = FC<Props> {
    PageWrapper {
        title = "Home"
        RouterLink {
            to = "/users"
            +"Users"
        }
    }
}

val UsersPage = FC<Props> {
    PageWrapper {
        title = "Users"
        backRoute = "/"
        UserManager {}
    }
}

val NotFoundPage = FC<Props> {
    h1 { +"ERROR: Page not found" }
}


val AppRouter = FC<Props> {
    BrowserRouter {
        Routes {
            Route {
                path = "/"
                element = createElement(IndexPage)
            }
            Route {
                path = "/users"
                element = createElement(UsersPage)
            }
            Route {
                path = "*"
                element = createElement(NotFoundPage)
            }
        }
    }
}
