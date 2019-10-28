package kotlinx.ast.common.map

import kotlinx.ast.common.AstFailure
import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast

fun AstResult<TreeMapResult>.flatten(): TreeMapResult {
    return if (this is AstSuccess) {
        result
    } else {
        TreeMapResult.Failure(this)
    }
}

sealed class TreeMapResult : AstResult<TreeMapResult> {
    data class Failure(
        override val errors: List<String>
    ) : TreeMapResult(), AstFailure<TreeMapResult> {
        constructor(result: AstResult<*>) : this(result.errorList())
    }

    object Keep : TreeMapResult(), AstSuccess<TreeMapResult> {
        override val result: TreeMapResult by lazy { this }
    }

    object Drop : TreeMapResult(), AstSuccess<TreeMapResult> {
        override val result: TreeMapResult by lazy { this }
    }

    data class Continue(val list: List<Ast>) : TreeMapResult(), AstSuccess<TreeMapResult> {
        constructor(ast: Ast) : this(listOf(ast))

        override val result: TreeMapResult by lazy { this }
    }

    data class Skip(val list: List<Ast>) : TreeMapResult(), AstSuccess<TreeMapResult> {
        constructor(ast: Ast) : this(listOf(ast))

        override val result: TreeMapResult by lazy { this }
    }
}
