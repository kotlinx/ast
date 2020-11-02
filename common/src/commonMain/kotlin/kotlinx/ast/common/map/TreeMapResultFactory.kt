package kotlinx.ast.common.map

import kotlinx.ast.common.AstFailure
import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast

interface TreeMapResultFactory<State> {
    fun <T> astContinue(result: List<T>): AstResult<State, List<T>>

    fun astContinue(first: Ast, vararg other: Ast): AstResult<State, List<Ast>> {
        return astSuccess(listOf(first) + other)
    }

    fun astKeep(): AstResult<State, List<Ast>>

    fun astDrop(): AstResult<State, List<Ast>>

    fun <T> String.astError(): AstResult<State, T> {
        return listOf(this).astError()
    }

    fun <T> List<String>.astError(): AstResult<State, T>

    fun <T> flatten(
        list: List<AstResult<State, T>>
    ): AstResult<State, List<T>> {
        val errors = list.filterIsInstance<AstFailure<State, T>>()
            .flatMap(AstFailure<State, T>::errorList)
        return when {
            errors.isEmpty() ->
                astSuccess(list.filterIsInstance<AstSuccess<State, T>>().map(AstSuccess<State, T>::success))
            else ->
                astFailure(errors)
        }
    }

    fun <T> astSuccess(result: T): AstResult<State, T>

    fun <Other, T> Other.astSuccess(result: T): AstResult<Other, T>

    fun <T> astFailure(errors: List<String>): AstResult<State, T>

    fun <Other, T> Other.astFailure(errors: List<String>): AstResult<Other, T>

    fun <T> astFailure(error: String): AstResult<State, T> {
        return astFailure(listOf(error))
    }

    fun <Other, T> Other.astFailure(error: String): AstResult<Other, T> {
        return astFailure(listOf(error))
    }

    fun <T1, T2> astFailure(result: AstFailure<State, T1>): AstResult<State, T2> {
        return astFailure(result.errors)
    }

    fun <Other, T1, T2> Other.astFailure(result: AstFailure<Other, T1>): AstResult<Other, T2> {
        return astFailure(result.errors)
    }
}
