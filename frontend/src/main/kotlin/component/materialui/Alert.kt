package component.materialui

import kotlinx.html.DIV
import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import materialui.components.MaterialElementBuilder
import materialui.components.getValue
import materialui.components.paper.PaperProps
import materialui.components.paper.enums.PaperStyle
import materialui.components.setValue
import org.w3c.dom.events.Event
import react.RBuilder
import react.RClass
import react.ReactElement

@JsModule("@material-ui/lab/Alert")
private external val alertModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val alertComponent: RClass<AlertProps> = alertModule.default

@Suppress("EnumEntryName")
enum class AlertVariant {
    standard, outlined, filled
}

@Suppress("EnumEntryName")
enum class AlertSeverity {
    info, success, error, warning
}

interface AlertProps : PaperProps {
    var role: String
    var icon: ReactElement? // TODO String?
    var iconMapping: Any? // TODO create class
    var closeText: String
    var action: ReactElement?
    var onCloseFunction: ((Event) -> Unit)?
    var variant: String
    var severity: String
    var color: String?
}

open class AlertElementBuilder<T: Tag, Props: AlertProps> internal constructor(
    type: RClass<Props>,
    classMap: List<Pair<Enum<*>, String>>,
    factory: (TagConsumer<Unit>) -> T
) : MaterialElementBuilder<T, Props>(type, classMap, factory) {
    fun Tag.classes(vararg classMap: Pair<PaperStyle, String>) {
        classes(classMap.map { it.first to it.second })
    }

    var Tag.variant: AlertVariant by materialProps
    var Tag.severity: AlertSeverity by materialProps
    var Tag.color: AlertSeverity? by materialProps
}

fun RBuilder.alert(vararg classMap: Pair<PaperStyle, String>, block: AlertElementBuilder<DIV, AlertProps>.() -> Unit) =
    child(AlertElementBuilder(alertComponent, classMap.toList()) { DIV(mapOf(), it) }.apply(block).create())
