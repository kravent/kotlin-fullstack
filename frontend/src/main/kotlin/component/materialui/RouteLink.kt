package component.materialui

import com.ccfraser.muirwik.components.MLinkProps
import com.ccfraser.muirwik.components.mLink
import react.RBuilder
import react.router.dom.LinkComponent
import styled.StyledHandler

interface MRouteLinkProps : MLinkProps {
    var to: String?
}

fun RBuilder.mRouteLink(to: String, handler: StyledHandler<MLinkProps> = {}) = mLink {
    attrs.asDynamic().component = LinkComponent::class.js
    attrs.asDynamic().to = to
    handler()
}
