package kotlinx.ast.common

import kotlinx.ast.common.ast.Ast
import kotlin.reflect.KClass

fun List<Ast>.filter(name: String): List<Ast> {
    return flatMap { ast ->
        ast.filter(name)
    }
}

fun List<Ast>.filter(
    names: Set<String>?,
    types: Set<KClass<out Ast>>? = null,
    drop: Set<String>? = null
): List<Ast> {
    return flatMap { ast ->
        ast.filter(names, types, drop)
    }
}

inline fun <reified A : Ast> List<Ast>.filter(): AstResult<List<A>> {
    val filtered = filterIsInstance<A>()
    return if (filtered.size == size) {
        astSuccess(filtered)
    } else {
        val bad = filterNot { it is A }
        astFailure(
            "found non-${A::class.qualifiedName} in children list: $bad"
        )
    }
}

fun Ast.filter(name: String): List<Ast> {
    return when (name) {
        this.description ->
            listOf(this)

        else ->
            emptyList()
    }
}

fun Ast.filter(
    names: Set<String>?,
    types: Set<KClass<out Ast>>?,
    drop: Set<String>?
): List<Ast> {
    return when {
        types != null && types.contains(this::class) ->
            listOf(this)

        names != null && names.contains(description) ->
            listOf(this)

        drop != null && !drop.contains(description) ->
            listOf(this)

        else ->
            emptyList()
    }
}
