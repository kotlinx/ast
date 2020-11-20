package kotlinx.ast.common.klass

import kotlinx.ast.common.AstResult
import kotlinx.ast.common.ast.*
import kotlinx.ast.common.filter
import kotlinx.ast.common.filter.byTypeNode
import kotlinx.ast.common.map.TreeMapContext

sealed class Klass() : AstGroup, AstWithRaw

sealed class KlassNode<Self : KlassNode<Self>>() : Klass(), AstNodeDefaults<Self> {
    abstract override fun detachRaw(): Self
}

data class KlassModifierGroup(val group: String)

data class KlassModifier(
    val modifier: String,
    val group: KlassModifierGroup,
    override val raw: RawAst?
) : Klass() {
    override val description: String = "KlassModifier($modifier, ${group.group})"

    override fun detachRaw(): KlassModifier {
        return copy(raw = null)
    }
}

val starProjection = KlassIdentifier(identifier = "*", raw = null)

fun List<KlassIdentifier>.identifierName(): String {
    return joinToString(
        separator = ".",
        transform = KlassIdentifier::rawName
    )
}

data class KlassComment(
    val comment: String,
    val type: KlassCommentType,
    override val raw: RawAst?
) : Klass() {
    override val description: String = "KlassComment(${type.rawName})"

    override fun detachRaw(): KlassComment {
        return copy(raw = null)
    }
}

data class KlassIdentifier(
    val identifier: String,
    val parameter: List<KlassIdentifier> = emptyList(),
    override val children: List<Ast> = emptyList(),
    val nullable: Boolean = false,
    override val raw: RawAst?
) : KlassNode<KlassIdentifier>(), AstWithAttributes {
    override val attributes: List<Ast> = parameter

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

    override fun detachRaw(): KlassIdentifier {
        return copy(raw = null)
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, KlassIdentifier> {
        return astSuccess(
            copy(
                parameter = children.filterIsInstance<KlassIdentifier>(),
                children = children.filter { ast ->
                    ast !is KlassIdentifier
                })
        )
    }
}

data class KlassString(
    override val children: List<StringComponent>,
    override val raw: RawAst?
) : KlassNode<KlassString>() {
    constructor(
        vararg children: StringComponent,
        raw: RawAst?
    ) : this(children.toList(), raw)

    override val description: String = "KlassString"

    override fun detachRaw(): KlassString {
        return copy(raw = null)
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, KlassString> {
        val stringComponents = children.filterIsInstance<StringComponent>()
        return if (stringComponents.size == children.size) {
            astSuccess(
                copy(children = stringComponents)
            )
        } else {
            "KlassString can only have StringComponent children".astError()
        }
    }
}

data class KlassAnnotation(
    val identifier: List<KlassIdentifier>,
    val arguments: List<KlassDeclaration>,
    override val raw: RawAst?
) : KlassNode<KlassAnnotation>(), AstWithAttributes {
    override val attributes: List<Ast> = identifier

    override val description: String = "KlassAnnotation(${identifier.identifierName()})"

    override val children: List<Ast> = arguments

    override fun detachRaw(): KlassAnnotation {
        return copy(raw = null)
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, KlassAnnotation> {
        val identifiers = children.filterIsInstance<KlassIdentifier>()
        val arguments = children.filterIsInstance<KlassDeclaration>()
        return if (identifiers.size + arguments.size == children.size) {
            astSuccess(
                copy(
                    identifier = identifier,
                    arguments = arguments
                )
            )
        } else {
            "KlassAnnotation can only have KlassIdentifier and KlassDeclaration children".astError()
        }
    }
}

data class KlassTypeParameter(
    val generic: KlassIdentifier,
    val base: List<KlassIdentifier>,
    override val raw: RawAst?
) : KlassNode<KlassTypeParameter>() {

    override val description: String = "KlassTypeParameter"

    override val children: List<Ast> = listOfNotNull(generic) + base

    override fun detachRaw(): KlassTypeParameter {
        return copy(raw = null)
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, KlassTypeParameter> {
        val identifiers = children.filterIsInstance<KlassIdentifier>()
        return if (identifiers.isNotEmpty() && children.size == identifiers.size) {
            astSuccess(
                copy(
                    generic = identifiers.first(),
                    base = identifiers.drop(1)
                )
            )
        } else {
            "KlassTypeParameter can only have KlassIdentifier children".astError()
        }
    }
}

data class KlassInheritance(
    val type: KlassIdentifier,
    val annotations: List<KlassAnnotation> = emptyList(),
    override val raw: RawAst?
) : KlassNode<KlassInheritance>() {
    override val description: String = "KlassInheritance"
    override val children: List<Ast> = listOf(
        type
    ) + annotations

    override fun detachRaw(): KlassInheritance {
        return copy(raw = null)
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, KlassInheritance> {
        val identifier = children.filterIsInstance<KlassIdentifier>().firstOrNull()
        val annotations = children.filterIsInstance<KlassAnnotation>()
        return if (identifier != null && annotations.size + 1 == children.size) {
            astSuccess(
                copy(
                    type = identifier,
                    annotations = annotations
                )
            )
        } else {
            "KlassInheritance requires one KlassIdentifier and KlassAnnotation children".astError()
        }
    }
}

data class KlassDeclaration(
    val keyword: String,
    val identifier: KlassIdentifier? = null,
    val type: KlassIdentifier? = null,
    val annotations: List<KlassAnnotation> = emptyList(),
    val modifiers: List<KlassModifier> = emptyList(),
    val parameter: List<KlassDeclaration> = emptyList(),
    val typeParameters: List<KlassTypeParameter> = emptyList(),
    val inheritance: List<KlassInheritance> = emptyList(),
    val expressions: List<Ast> = emptyList(),
    val comments: List<KlassComment> = emptyList(),
    override val raw: RawAst?
) : KlassNode<KlassDeclaration>(), AstWithAttributes {
    override val attributes: List<Ast> = listOfNotNull(identifier, type)

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
        expressions,
        comments,
    ).flatten()

    override fun detachRaw(): KlassDeclaration {
        return copy(raw = null)
    }

    override fun <State> TreeMapContext<State>.withChildren(children: List<Ast>): AstResult<State, KlassDeclaration> {
        return toKlassDeclaration(keyword, children).map { declaration ->
            declaration.copy(
                raw = raw
            )
        }
    }
}

private val klassDeclarationKeywords: Map<String, String> = mapOf(
    "VAR" to "var",
    "VAL" to "val",
    "CLASS" to "class",
    "INTERFACE" to "interface",
    "OBJECT" to "object",
    "primaryConstructor" to "constructor",
    "valueArgument" to "argument",
    "classParameter" to "parameter",
)

fun <State> TreeMapContext<State>.toKlassDeclaration(
    node: AstNode,
    expressions: ((List<Ast>) -> AstResult<State, List<Ast>>)? = null,
): AstResult<State, KlassDeclaration> {
    val descriptions = node.children.descriptions().toSet() + node.description
    val keywords = klassDeclarationKeywords.mapNotNull { (key, value) ->
        if (descriptions.contains(key)) {
            value
        } else {
            null
        }
    }
    return if (keywords.isEmpty()) {
        "toKlassDeclaration: found no keyword!".astError()
    } else {
        node.filterChildren(byTypeNode).flatMap { filtered ->
            recursive(filtered.children)
        }.flatMap { result ->
            toKlassDeclaration(keywords.first(), result, expressions, attachRaw(node))
        }
    }
}

fun <State> TreeMapContext<State>.toKlassDeclaration(
    keyword: String,
    ast: List<Ast>,
    expressions: ((List<Ast>) -> AstResult<State, List<Ast>>)? = null,
    raw: RawAst? = null
): AstResult<State, KlassDeclaration> {
    val identifiers = ast.filterIsInstance<KlassIdentifier>()

    val identifier = identifiers.getOrNull(0)
    val type = identifiers.getOrNull(1)

    val annotations = ast.filterIsInstance<KlassAnnotation>()
    val modifiers = ast.filterIsInstance<KlassModifier>()
    val parameter = ast.filterIsInstance<KlassDeclaration>()
    val typeParameters = ast.filterIsInstance<KlassTypeParameter>()
    val inheritance = ast.filterIsInstance<KlassInheritance>()
    val comments = ast.filterIsInstance<KlassComment>()

    val used = setOfNotNull(identifier, type) +
            annotations +
            modifiers +
            parameter +
            typeParameters +
            inheritance +
            comments
    val remaining = ast - used
    return (expressions?.invoke(remaining) ?: astSuccess(remaining)).map { other ->
        KlassDeclaration(
            keyword = keyword,
            identifier = identifier,
            type = type,
            annotations = annotations,
            modifiers = modifiers,
            parameter = parameter,
            typeParameters = typeParameters,
            inheritance = inheritance,
            expressions = other,
            comments = comments,
            raw = raw,
        )
    }
}

fun List<Ast>.expressions(): List<Ast> {
    return filter("expression").filterIsInstance<AstNode>().flatMap(AstNode::children)
}

fun List<Ast>.descriptions(): List<String> {
    return map(Ast::description)
}
