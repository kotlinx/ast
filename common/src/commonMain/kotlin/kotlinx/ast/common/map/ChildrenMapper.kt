package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.astSuccess
import kotlinx.ast.common.filter

interface ChildrenMapper {
    fun map(treeMap: TreeMapWithRawAst, node: AstNode): AstResult<TreeMapResult>
}

private data class ChildrenMapperImpl(
    val mappers: List<TreeMapNodeMapper> = emptyList()
) : ChildrenMapper {
    override fun map(treeMap: TreeMapWithRawAst, node: AstNode): AstResult<TreeMapResult> {
        return mappers.fold(
            TreeMapResult.Keep as AstResult<TreeMapResult>
        ) { result, mapper ->
            when {
                result is AstSuccess && result.result == TreeMapResult.Keep ->
                    mapper.map(treeMap, node)
                else ->
                    result
            }
        }
    }
}

data class ChildrenMapperBuilder(
    val mappers: List<TreeMapNodeMapper> = emptyList()
) {
    fun build(): ChildrenMapper {
        return ChildrenMapperImpl(mappers)
    }

    inline fun <reified A : Ast> mapList(
        crossinline mapper: TreeMapContext.(List<A>) -> AstResult<TreeMapResult>
    ): ChildrenMapperBuilder {
        return copy(
            mappers = mappers + TreeMapNodeMapper { node ->
                node.children.filter<A>().flatMap { children ->
                    mapper(TreeMapContext(this, node, attachRawAst = attachRawAst), children)
                }
            }
        )
    }

    inline fun <reified A : Ast> map(
        crossinline mapper: TreeMapContext.(A) -> AstResult<TreeMapResult>
    ): ChildrenMapperBuilder {
        return copy(
            mappers = mappers + TreeMapNodeMapper { node ->
                val children = node.children
                if (children.size == 1) {
                    val a = children[0]
                    if (a is A) {
                        mapper(TreeMapContext(this, node, attachRawAst = attachRawAst), a)
                    } else {
                        TreeMapResult.Keep
                    }
                } else {
                    astSuccess(TreeMapResult.Keep)
                }
            }
        )
    }

    inline fun <reified A : Ast, reified B : Ast> map(
        crossinline mapper: TreeMapContext.(A, B) -> AstResult<TreeMapResult>
    ): ChildrenMapperBuilder {
        return copy(
            mappers = mappers + TreeMapNodeMapper { node ->
                val children = node.children
                if (children.size == 2) {
                    val a = children[0]
                    val b = children[1]
                    if (a is A && b is B) {
                        mapper(TreeMapContext(this, node, attachRawAst = attachRawAst), a, b)
                    } else {
                        TreeMapResult.Keep
                    }
                } else {
                    TreeMapResult.Keep
                }
            }
        )
    }

    inline fun <reified A : Ast> filterChildren(): ChildrenMapperBuilder {
        return mapList { components: List<A> ->
            TreeMapResult.Continue(components)
        }
    }
}
