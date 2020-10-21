package kotlinx.ast.common

import kotlinx.ast.common.map.TreeMapResultFactory

interface AstResult<State, out T> {
    val state: State

    fun get(): T

    fun <T2> map(mapper: TreeMapResultFactory<State>.(T) -> T2): AstResult<State, T2>

    fun <Other, T2> flatMap(mapper: TreeMapResultFactory<State>.(T) -> AstResult<Other, T2>): AstResult<Other, T2>

    fun onSuccess(callback: TreeMapResultFactory<State>.(T) -> Unit): AstResult<State, T>

    fun onFailure(callback: TreeMapResultFactory<State>.(List<String>) -> Unit): AstResult<State, T>

    fun errorList(): List<String> = emptyList()
}

interface AstSuccess<State, T> : AstResult<State, T> {
    val factory: TreeMapResultFactory<State>
    val success: T

    override fun get(): T = success

    override fun onSuccess(
        callback: TreeMapResultFactory<State>.(T) -> Unit
    ): AstResult<State, T> {
        factory.callback(success)
        return this
    }

    override fun onFailure(
        callback: TreeMapResultFactory<State>.(List<String>) -> Unit
    ): AstResult<State, T> {
        return this
    }
}

interface AstFailure<State, T> : AstResult<State, T> {
    val factory: TreeMapResultFactory<State>
    val errors: List<String>

    override fun errorList(): List<String> {
        return errors
    }

    override fun get(): Nothing {
        throw IllegalStateException("found errors: $errors")
    }

    override fun onSuccess(
        callback: TreeMapResultFactory<State>.(T) -> Unit
    ): AstResult<State, T> {
        return this
    }

    override fun onFailure(
        callback: TreeMapResultFactory<State>.(List<String>) -> Unit
    ): AstResult<State, T> {
        factory.callback(errors)
        return this
    }
}
