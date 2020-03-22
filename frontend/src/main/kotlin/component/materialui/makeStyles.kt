package component.materialui

import kotlinext.js.jsObject
import kotlinx.css.CSSBuilder
import kotlinx.css.hyphenize
import materialui.styles.StylesSet
import materialui.styles.muitheme.MuiTheme

@JsModule("@material-ui/core/styles")
private external val stylesModule: dynamic

@Suppress("UnsafeCastFromDynamic")
private val makeStyles: (dynamic, dynamic) -> (() -> dynamic) = stylesModule.makeStyles

private class StylesBuilder(
    override val theme: MuiTheme,
    override val css: MutableMap<String, CSSBuilder> = mutableMapOf()
) : StylesSet {
    fun toDynamic() = kotlinext.js.js {
        css.forEach { (key, value) -> this[key] = value.toDynamic }
    } as Any
}

private val CSSBuilder.toDynamic: Any
    get() = kotlinext.js.js {
        rules.forEach {
            this[it.selector] = CSSBuilder().apply(it.block).toDynamic
        }

        declarations.forEach { (key, value) ->
            this[key.hyphenize()] = when (value) {
                is CSSBuilder -> value.toDynamic
                else -> value.toString()
            }
        }
    } as Any

data class MakeStylesOptions(
    val defaultTheme: String? = null,
    val name: String? = null,
    val flip: Boolean? = null,
    val extraOptions: Map<String, Any?> = emptyMap()
)

fun makeStyles(options: MakeStylesOptions = MakeStylesOptions(), styleSet: StylesSet.() -> Unit) = makeStyles(
    { theme: MuiTheme ->
        StylesBuilder(theme).apply(styleSet).toDynamic()
    },
    jsObject {} // TODO read from options
)
