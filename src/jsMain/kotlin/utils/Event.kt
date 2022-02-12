package utils

import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import react.dom.events.FormEvent

inline fun handleInputEvent(
    crossinline callback: (target: HTMLInputElement) -> Unit
): (event: FormEvent<HTMLDivElement>) -> Unit =
    { event ->
        when (val target = event.target) {
            is HTMLInputElement -> callback(target)
        }
    }
