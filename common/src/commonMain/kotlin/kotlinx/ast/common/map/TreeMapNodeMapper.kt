package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.AstNode

data class TreeMapNodeMapper(val map: TreeMap.(AstNode) -> AstResult<TreeMapResult>)
