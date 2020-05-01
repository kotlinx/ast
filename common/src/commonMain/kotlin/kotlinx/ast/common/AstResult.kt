package kotlinx.ast.common

inline fun <reified T> List<AstResult<T>>.flatten(): AstResult<List<T>> {
    val errors = filterIsInstance<AstFailure<T>>()
        .flatMap(AstFailure<T>::errorList)
    return when {
        errors.isEmpty() ->
            astSuccess(filterIsInstance<AstSuccess<T>>().map(AstSuccess<T>::result))
        else ->
            astFailure(errors)
    }
}

fun <T> astSuccess(result: T): AstResult<T> = DefaultAstSuccess(result)

fun <T> astFailure(error: String): AstResult<T> = astFailure(listOf(error))

fun <T> astFailure(errors: List<String>): AstResult<T> = DefaultAstFailure(errors)

fun <T1, T2> astFailure(result: AstFailure<T1>): AstResult<T2> {
    return astFailure(result.errors)
}

interface AstResult<T> {
    fun get(): T

    fun <T2> map(mapper: (T) -> T2): AstResult<T2>

    fun <T2> flatMap(mapper: (T) -> AstResult<T2>): AstResult<T2>

    fun onSuccess(callback: (T) -> Unit): AstResult<T>

    fun onFailure(callback: (List<String>) -> Unit): AstResult<T>

    fun errorList(): List<String> = emptyList()

    fun <T2> zip(other: AstResult<T2>): AstResult<Pair<T, T2>> {
        return when {
            this is AstSuccess<T> && other is AstSuccess<T2> ->
                astSuccess(Pair(result, other.result))
            else ->
                astFailure(errorList() + other.errorList())
        }
    }
}

interface AstSuccess<T> : AstResult<T> {
    val result: T

    override fun get(): T = result

    override fun <T2> map(mapper: (T) -> T2): AstResult<T2> {
        return DefaultAstSuccess(mapper(result))
    }

    override fun <T2> flatMap(mapper: (T) -> AstResult<T2>): AstResult<T2> {
        return mapper(result)
    }

    override fun onSuccess(callback: (T) -> Unit): AstResult<T> {
        callback(result)
        return this
    }

    override fun onFailure(callback: (List<String>) -> Unit): AstResult<T> {
        return this
    }

    companion object {
        operator fun <T> invoke(result: T): AstSuccess<T> {
            return DefaultAstSuccess(result)
        }
    }
}

data class DefaultAstSuccess<T>(override val result: T) : AstSuccess<T>

interface AstFailure<T> : AstResult<T> {
    val errors: List<String>

    override fun errorList(): List<String> {
        return errors
    }

    override fun get(): Nothing {
        throw IllegalStateException("found errors: $errors")
    }

    override fun <T2> map(mapper: (T) -> T2): AstResult<T2> {
        return astFailure(this)
    }

    override fun <T2> flatMap(mapper: (T) -> AstResult<T2>): AstResult<T2> {
        return astFailure(this)
    }

    override fun onSuccess(callback: (T) -> Unit): AstResult<T> {
        return this
    }

    override fun onFailure(callback: (List<String>) -> Unit): AstResult<T> {
        callback(errors)
        return this
    }

    companion object {
        operator fun <T> invoke(errors: List<String>): AstFailure<T> {
            return DefaultAstFailure(errors)
        }
    }
}

data class DefaultAstFailure<T>(override val errors: List<String>) : AstFailure<T>
