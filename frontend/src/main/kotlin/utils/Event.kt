package utils

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

inline fun <reified TargetType : EventTarget> withTarget(crossinline callback: (target: TargetType) -> Unit): (event: Event) -> Unit =
    { event ->
        when (val target = event.target) {
            is TargetType -> callback(target)
        }
    }
