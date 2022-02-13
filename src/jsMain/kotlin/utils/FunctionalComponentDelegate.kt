package utils

import react.ChildrenBuilder
import react.FC
import react.Props
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

private class FunctionalComponentDelegate<P : Props>(
    private val block: ChildrenBuilder.(props: P) -> Unit,
) : ReadOnlyProperty<Any?, FC<P>> {
    var component: FC<P>? = null
    override fun getValue(thisRef: Any?, property: KProperty<*>): FC<P> {
        return component?.let { return it }
            ?: FC(property.name, block).also { component = it }
    }
}

fun NC(
    block: ChildrenBuilder.(props: Props) -> Unit,
): ReadOnlyProperty<Any?, FC<Props>> = FunctionalComponentDelegate(block)

fun <P : Props>NC(
    block: ChildrenBuilder.(props: P) -> Unit,
): ReadOnlyProperty<Any?, FC<P>> = FunctionalComponentDelegate(block)
