package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.DefaultAstNode
import kotlinx.ast.common.astFailure
import kotlinx.ast.common.filter
import kotlin.reflect.KClass

data class TreeMapMapper(
    val mapper: List<TreeMapAstMapper> = emptyList()
) {
    inline fun <reified A : Ast> map(
        crossinline cb: TreeMapContext.(A) -> AstResult<TreeMapResult>
    ): TreeMapMapper {
        return copy(
            mapper = mapper + TreeMapAstMapper {
                if (context is A) {
                    cb(context)
                } else {
                    TreeMapResult.Keep
                }
            }
        )
    }

    inline fun <reified A : Ast> map(
        name: String,
        crossinline cb: TreeMapContext.(A) -> AstResult<TreeMapResult>
    ): TreeMapMapper {
        return map { ast: A ->
            if (ast.description == name) {
                cb(ast)
            } else {
                TreeMapResult.Keep
            }
        }
    }

    inline fun <reified A : Ast> map(
        names: Set<String>,
        crossinline cb: TreeMapContext.(A) -> AstResult<TreeMapResult>
    ): TreeMapMapper {
        return map { node: A ->
            if (names.contains(node.description)) {
                cb(node)
            } else {
                TreeMapResult.Keep
            }
        }
    }

    inline fun <reified A : Ast> filter(
        crossinline cb: TreeMapContext.(A) -> Boolean
    ): TreeMapMapper {
        return map { ast: A ->
            if (cb(ast)) {
                TreeMapResult.Keep
            } else {
                TreeMapResult.Drop
            }
        }
    }

    inline fun <reified A : Ast> excludeNames(vararg names: String): TreeMapMapper {
        val set = names.toSet()
        return filter { ast: A ->
            !set.contains(ast.description)
        }
    }

    fun flattenNames(vararg names: String): TreeMapMapper {
        val set = names.toSet()
        return map { node: AstNode ->
            if (set.contains(node.description)) {
                TreeMapResult.Continue(node.children)
            } else {
                TreeMapResult.Keep
            }
        }
    }

    fun flattenNamesNoChildren(vararg names: String): TreeMapMapper {
        val set = names.toSet()
        return map { node: AstNode ->
            if (set.contains(node.description) && node.children.size <= 1) {
                TreeMapResult.Continue(node.children)
            } else {
                TreeMapResult.Keep
            }
        }
    }

    fun filterChildren(
        flatten: Boolean,
        childrenNamesToKeep: Map<String, Set<String>> = emptyMap(),
        childrenTypesToKeep: Map<String, Set<KClass<out Ast>>> = emptyMap(),
        childrenNamesToDrop: Map<String, Set<String>> = emptyMap()
    ): TreeMapMapper {
        return map { node: DefaultAstNode ->
            val keepNames = childrenNamesToKeep[node.description]
            val keepTypes = childrenTypesToKeep[node.description]
            val dropNames = childrenNamesToDrop[node.description]
            when {
                keepNames == null && keepTypes == null && dropNames == null ->
                    TreeMapResult.Keep

                flatten ->
                    TreeMapResult.Continue(
                        node.children.filter(
                            keepNames,
                            keepTypes,
                            dropNames
                        )
                    )

                else ->
                    TreeMapResult.Skip(
                        node.copy(
                            children = node.children.filter(
                                keepNames,
                                keepTypes,
                                dropNames
                            )
                        )
                    )
            }
        }
    }

    fun mapChildren(
        name: String,
        treeMap: Boolean = false,
        childrenMapper: (ChildrenMapperBuilder) -> ChildrenMapperBuilder
    ): TreeMapMapper {
        val mapper = childrenMapper(ChildrenMapperBuilder()).build()
        return map(name) { node: AstNode ->
            if (treeMap) {
                if (node is DefaultAstNode) {
                    treeMap(node.children, attachRawAst = attachRawAst).flatMap { children ->
                        mapper.map(
                            this, node.copy(
                                children = children
                            )
                        )
                    }
                } else {
                    astFailure("TreeMapMapper.mapChildren.treeMap is true, but node is no DefaultAstNode")
                }
            } else {
                mapper.map(this, node)
            }
        }
    }

    fun mapChildren(
        names: Set<String>,
        childrenMapper: (ChildrenMapperBuilder) -> ChildrenMapperBuilder
    ): TreeMapMapper {
        val mapper = childrenMapper(ChildrenMapperBuilder()).build()
        return map(names) { node: AstNode ->
            mapper.map(this, node)
        }
    }

    inline fun <reified A : Ast> treeMap(
        name: String,
        noinline mapper: (List<A>) -> TreeMapResult
    ): TreeMapMapper {
        return treeMap(name, { it.filter() }, mapper)
    }

    inline fun <reified A : Ast> treeMap(
        name: String,
        // it seems to be a kotlin compiler bug, but calling filter inline here
        // will fail because AstNode is inferred
        noinline filter: (List<Ast>) -> AstResult<List<A>>,
        noinline mapper: (List<A>) -> TreeMapResult
    ): TreeMapMapper {
        return map(name) { node: AstNode ->
            treeMap(node.children, attachRawAst = attachRawAst).flatMap(filter).flatMap(mapper)
        }
    }
}
