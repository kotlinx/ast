package kotlinx.ast.common.util

fun <K, V> Map<K, Set<V>>.add(key: K, value: V): Map<K, Set<V>> {
    val values = getOrElse(key, { emptySet() }) + value
    return this + (key to values)
}

fun <K, V> Map<K, Set<V>>.remove(key: K, value: V): Map<K, Set<V>> {
    val values = getOrElse(key, { emptySet() }) - value
    return if (values.isEmpty()) {
        this + (key to values)
    } else {
        this - key
    }
}
