package component

import component.main.PageWrapper
import component.user.UserManager
import react.createElement
import react.dom.html.ReactHTML.h1
import react.router.Route
import react.router.Routes
import react.router.dom.BrowserRouter
import utils.NC
import react.router.dom.Link as RouterLink

val IndexPage by NC {
    PageWrapper {
        title = "Home"
        RouterLink {
            to = "/users"
            +"Users"
        }
    }
}

val UsersPage by NC {
    PageWrapper {
        title = "Users"
        backRoute = "/"
        UserManager {}
    }
}

val NotFoundPage by NC {
    h1 { +"ERROR: Page not found" }
}


val AppRouter by NC {
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
