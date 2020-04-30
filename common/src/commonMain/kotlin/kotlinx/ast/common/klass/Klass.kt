package kotlinx.ast.common.klass

import kotlinx.ast.common.AstChannel
import kotlinx.ast.common.ast.*

sealed class Klass() : AstGroup {
    abstract val raw: Ast
}

data class KlassModifierGroup(val group: String)

data class KlassModifier(
    override val raw: Ast,
    val modifier: String,
    val group: KlassModifierGroup
) : Klass() {
    override val description: String = "KlassModifier($modifier, ${group.group})"
}

val starProjection = KlassIdentifier(
    raw = DefaultAstNode(
        "typeProjection", listOf(
            DefaultAstTerminal("MULT", "*", AstChannel(0, "DEFAULT_TOKEN_CHANNEL"))
        )
    ),
    identifier = "*"
)

fun List<KlassIdentifier>.identifierName(): String {
    return joinToString(
        separator = ".",
        transform = KlassIdentifier::rawName
    )
}

data class KlassIdentifier(
    override val raw: Ast,
    val identifier: String,
    val parameter: List<KlassIdentifier> = emptyList(),
    val nullable: Boolean = false
) : Klass() {
    val rawName: String = listOfNotNull(
        identifier,
        if (parameter.isEmpty()) {
            null
        } else {
            parameter.map(KlassIdentifier::rawName).joinToString(
                prefix = "<",
                separator = ", ",
                postfix = ">"
            )
        },
        if (nullable) {
            "?"
        } else {
            null
        }
    ).joinToString("")

    override val description: String = "KlassIdentifier($rawName)"

    fun parameterizedBy(parameter: KlassIdentifier): KlassIdentifier {
        return copy(
            parameter = this.parameter + parameter
        )
    }

    fun parameterizedBy(vararg parameter: KlassIdentifier): KlassIdentifier {
        return parameter.fold(this, KlassIdentifier::parameterizedBy)
    }
}

data class KlassString(
    override val raw: Ast,
    override val children: List<StringComponent>
) : Klass(), AstNode {
    constructor(
        vararg children: StringComponent,
        raw: Ast
    ) : this(raw, children.toList())

    override val description: String = "KlassString"
}

data class KlassAnnotation(
    override val raw: Ast,
    val identifier: List<KlassIdentifier>,
    val arguments: List<KlassDeclaration>
) : Klass(), AstNode {
    override val description: String = "KlassAnnotation(${identifier.identifierName()})"

    override val children: List<Ast> = arguments
}

data class KlassTypeParameter(
    override val raw: Ast,
    val generic: KlassIdentifier,
    val base: KlassIdentifier?
) : Klass(), AstNode {
    override val description: String = "KlassTypeParameter"

    override val children: List<Ast> = listOfNotNull(
        generic,
        base
    )
}

data class KlassInheritance(
    override val raw: Ast,
    val type: KlassIdentifier,
    val annotations: List<KlassAnnotation> = emptyList()
) : Klass(), AstNode {
    override val description: String = "KlassInheritance"
    override val children: List<Ast> = listOf(
        type
    ) + annotations
}

data class KlassDeclaration(
    override val raw: Ast,
    val keyword: String,
    val identifier: KlassIdentifier? = null,
    val type: KlassIdentifier? = null,
    val annotations: List<KlassAnnotation> = emptyList(),
    val modifiers: List<KlassModifier> = emptyList(),
    val parameter: List<KlassDeclaration> = emptyList(),
    val typeParameters: List<KlassTypeParameter> = emptyList(),
    val inheritance: List<KlassInheritance> = emptyList(),
    val expressions: List<Ast> = emptyList()
) : Klass(), AstNode {
    override val description: String =
        listOfNotNull(
            keyword,
            identifier?.rawName,
            type?.rawName
        ).joinToString(" ", "KlassDeclaration(", ")")

    override val children: List<Ast> = listOf(
        annotations,
        modifiers,
        parameter,
        typeParameters,
        inheritance,
        expressions
    ).flatten()
}
