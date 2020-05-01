package utils

import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

inline fun <reified EventType : Event> withEvent(
    crossinline callback: (event: EventType) -> Unit
): (event: Event) -> Unit = { callback(it.unsafeCast<EventType>()) }

inline fun <reified TargetType : EventTarget> withTarget(
    crossinline callback: (target: TargetType) -> Unit
): (event: Event) -> Unit = { callback(it.target.unsafeCast<TargetType>()) }
