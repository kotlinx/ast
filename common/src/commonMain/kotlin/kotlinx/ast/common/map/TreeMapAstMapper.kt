package kotlinx.ast.common.map

import kotlinx.ast.common.AstResult

data class TreeMapAstMapper(val map: TreeMapContext.() -> AstResult<TreeMapResult>)
