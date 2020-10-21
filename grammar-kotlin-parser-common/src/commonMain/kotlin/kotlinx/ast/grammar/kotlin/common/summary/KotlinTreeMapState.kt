package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.klass.KlassComment

data class KotlinTreeMapState(
    val comments: List<KlassComment> = emptyList()
)
