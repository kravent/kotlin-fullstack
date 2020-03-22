package component.materialui

import kotlinx.html.P
import materialui.components.link.LinkElementBuilder
import materialui.components.link.link
import react.RBuilder
import react.router.dom.LinkComponent

fun RBuilder.mRouteLink(to: String, block: LinkElementBuilder<P>.() -> Unit) = link {
    attrs.asDynamic().component = LinkComponent::class.js
    attrs.asDynamic().to = to
    block()
}
