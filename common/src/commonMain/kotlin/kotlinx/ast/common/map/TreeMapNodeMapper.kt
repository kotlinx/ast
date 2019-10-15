package kotlinx.ast.common.map

import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.AstResult

data class TreeMapNodeMapper(val map: TreeMap.(AstNode) -> AstResult<TreeMapResult>)
