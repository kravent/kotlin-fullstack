package component.materialui

import com.ccfraser.muirwik.components.*
import react.RBuilder
import react.RComponent
import react.RState
import react.ReactElement
import styled.StyledHandler

@JsModule("@material-ui/lab/Alert")
private external val alertModule: dynamic
@Suppress("UnsafeCastFromDynamic")
private val alertComponent: RComponent<MAlertProps, RState> = alertModule.default

@Suppress("EnumEntryName")
enum class MAlertVariant {
    standard, outlined, filled
}

@Suppress("EnumEntryName")
enum class MAlertSeverity {
    info, success, error, warning
}

interface MAlertProps : MPaperProps {
    var role: String
    var icon: ReactElement? // TODO String?
    var iconMapping: Any? // TODO create class
    var closeText: String
    var action: ReactElement?
    var onClose: SimpleEvent?
}
var MAlertProps.variant: MAlertVariant by EnumPropToString(MAlertVariant.values())
var MAlertProps.severity: MAlertSeverity by EnumPropToString(MAlertSeverity.values())
var MAlertProps.color: MAlertSeverity? by EnumPropToStringNullable(MAlertSeverity.values())

fun RBuilder.mAlert(
    text: String? = null,
    role: String = "alert",
    variant: MAlertVariant = MAlertVariant.standard,
    severity: MAlertSeverity = MAlertSeverity.success,
    color: MAlertSeverity? = null,
    iconName: String? = null,
    action: ReactElement? = null,
    onClose: SimpleEvent? = null,
    addAsChild: Boolean = true,
    className: String? = null,
    handler: StyledHandler<MAlertProps>? = null
) = createStyled(alertComponent, addAsChild) {
    attrs.role = role
    attrs.variant = variant
    attrs.severity = severity
    attrs.color = color
    attrs.icon = iconName?.let { mIcon(it) }
    attrs.action = action
    attrs.onClose = onClose

    text?.let {childList.add(it)}

    setStyledPropsAndRunHandler(className, handler)
}
