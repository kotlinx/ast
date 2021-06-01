package kotlinx.ast.grammar.kotlin.common.summary

import kotlinx.ast.common.AstSuccess
import kotlinx.ast.common.ast.Ast
import kotlinx.ast.common.ast.AstNode
import kotlinx.ast.common.ast.AstTerminal
import kotlinx.ast.common.ast.DefaultAstTerminal
import kotlinx.ast.common.filter
import kotlinx.ast.common.filter.TreeFilterAll
import kotlinx.ast.common.filter.byDescription
import kotlinx.ast.common.klass.*
import kotlinx.ast.common.map.TreeMapBuilder
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarAstChannels

val kotlinTreeMapBuilder = TreeMapBuilder<KotlinTreeMapState>()
    .convert(
        filter = TreeFilterAll
    ) { terminal: DefaultAstTerminal ->
        if (terminal.channel == KotlinGrammarAstChannels.hidden) {
            astDrop()
        } else {
            astKeep()
        }
    }

// /**
//  * Kotlin syntax grammar in ANTLR4 notation
//  */
//
// parser grammar KotlinParser;
//
// options { tokenVocab = KotlinLexer; }
//
// // SECTION: general

// kotlinFile
//     : shebangLine? NL* fileAnnotation* packageHeader importList topLevelObject* EOF
//     ;
    .convert(
        filter = byDescription("kotlinFile")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// script
//     : shebangLine? NL* fileAnnotation* packageHeader importList (statement semi)* EOF
//     ;

// shebangLine
//     : ShebangLine NL+
//     ;

// fileAnnotation
//     : (AT_NO_WS | AT_PRE_WS) FILE NL* COLON NL* (LSQUARE unescapedAnnotation+ RSQUARE | unescapedAnnotation) NL*
//     ;

// packageHeader
//     : (PACKAGE identifier semi?)?
//     ;
    .convert(
        filter = byDescription("packageHeader")
    ) { node: AstNode ->
        recursiveFlatten(
            node,
            filter = byDescription("identifier")
        ).flatMap { result ->
            val identifier = result.filterIsInstance<KlassIdentifier>()
            astContinue(PackageHeader(identifier))
        }
    }

// importList
//     : importHeader*
//     ;
    .convert(
        filter = byDescription("importList")
    ) { node: AstNode ->
        recursiveChildren(node)
    }

// importHeader
//     : IMPORT identifier (DOT MULT | importAlias)? semi?
//     ;
    .convert(
        filter = byDescription("importHeader")
    ) { node: AstNode ->
        recursiveFlatten(
            node,
            filter = byDescription(
                "identifier",
                "importAlias",
                "MULT",
                "AS"
            )
        ).flatMap { result ->
            val identifier = result.filterIsInstance<KlassIdentifier>()
            astContinue(
                when {
                    result.any(byDescription("MULT")::matches) ->
                        Import(
                            identifier = identifier,
                            starProjection = true,
                        )
                    result.any(byDescription("AS")::matches) -> {
                        Import(
                            identifier = identifier.dropLast(1),
                            alias = identifier.last(),
                        )
                    }
                    else ->
                        Import(
                            identifier = identifier,
                        )
                }
            )
        }
    }

// importAlias
//     : AS simpleIdentifier
//     ;
    .convert(
        filter = byDescription("importAlias")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// topLevelObject
//     : declaration semis?
//     ;
    .convert(
        filter = byDescription("topLevelObject")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("declaration"))
    }

// typeAlias
//     : modifiers? TYPE_ALIAS NL* simpleIdentifier (NL* typeParameters)? NL* ASSIGNMENT NL* type
//     ;

// declaration
//     : classDeclaration
//     | objectDeclaration
//     | functionDeclaration
//     | propertyDeclaration
//     | typeAlias
//     ;
    .convert(
        filter = byDescription("declaration")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// // SECTION: classes

// classDeclaration
//     : modifiers? (CLASS | (FUN NL*)? INTERFACE) NL* simpleIdentifier
//     (NL* typeParameters)? (NL* primaryConstructor)?
//     (NL* COLON NL* delegationSpecifiers)?
//     (NL* typeConstraints)?
//     (NL* classBody | NL* enumClassBody)?
//     ;
    .convert(
        filter = byDescription("classDeclaration")
    ) { node: AstNode ->
        toKlassDeclaration(node) { result ->
            astContinue(
                result.filter(
                    setOf(
                        "primaryConstructor",
                        "typeConstraints",
                        "classBody",
                        "enumClassBody"
                    )
                )
            )
        }.toAstList()
    }

// primaryConstructor
//     : (modifiers? CONSTRUCTOR NL*)? classParameters
//     ;
    .convert(
        filter = byDescription("primaryConstructor")
    ) { node: AstNode ->
        toKlassDeclaration(node).toAstList()
    }

// classBody
//     : LCURL NL* classMemberDeclarations NL* RCURL
//     ;
    .convert(
        filter = byDescription("classBody")
    ) { node: AstNode ->
        recursiveChildren(node, filter = byDescription("classMemberDeclarations"))
    }

// classParameters
//     : LPAREN NL* (classParameter (NL* COMMA NL* classParameter)* (NL* COMMA)?)? NL* RPAREN
//     ;
    .convert(
        filter = byDescription("classParameters")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("classParameter"))
    }

// classParameter
//     : modifiers? (VAL | VAR)? NL* simpleIdentifier COLON NL* type (NL* ASSIGNMENT NL* expression)?
//     ;
    .convert(
        filter = byDescription("classParameter")
    ) { node: AstNode ->
        node.filterChildren(filter = !byDescription("COLON"))
            .flatMap { filtered ->
                toKlassDeclaration(filtered) { ast ->
                    astContinue(ast.expressions())
                }
            }.toAstList()
    }

// delegationSpecifiers
//     : annotatedDelegationSpecifier (NL* COMMA NL* annotatedDelegationSpecifier)*
//     ;
    .convert(
        filter = byDescription("delegationSpecifiers")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("annotatedDelegationSpecifier"))
    }

// delegationSpecifier
//     : constructorInvocation
//     | explicitDelegation
//     | userType
//     | functionType
//     ;
    .convert(
        filter = byDescription("delegationSpecifier")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// constructorInvocation
//     : userType valueArguments
//     ;
    .convert(
        filter = byDescription("constructorInvocation")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// annotatedDelegationSpecifier
//     : annotation* NL* delegationSpecifier
//     ;
    .convert(
        filter = byDescription("annotatedDelegationSpecifier")
    ) { node: AstNode ->
        recursiveFlatten(node).flatMap { result ->
            val identifier = result.filterIsInstance<KlassIdentifier>()
            if (identifier.size == 1) {
                astContinue(
                    KlassInheritance(
                        type = identifier.first(),
                        annotations = result.filterIsInstance<KlassAnnotation>()
                    )
                )
            } else {
                astContinue(result)
            }
        }
    }

// explicitDelegation
//     : (userType | functionType) NL* BY NL* expression
//     ;

// typeParameters
//     : LANGLE NL* typeParameter (NL* COMMA NL* typeParameter)* (NL* COMMA)? NL* RANGLE
//     ;
    .convert(
        filter = byDescription("typeParameters")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("typeParameter"))
    }

// typeParameter
//     : typeParameterModifiers? NL* simpleIdentifier (NL* COLON NL* type)?
//     ;
    .convert(
        filter = byDescription("typeParameter")
    ) { node: AstNode ->
        recursiveFlatten(
            node,
            filter = byDescription("simpleIdentifier", "type")
        ).flatMap { result ->
            val identifiers = result.filterIsInstance<KlassIdentifier>()
            val generic = identifiers.firstOrNull()
            val base = identifiers.drop(1)
            if (generic == null) {
                "failed to parse typeParameter".astError()
            } else {
                astContinue(KlassTypeParameter(generic, base))
            }
        }
    }

// typeConstraints
//     : WHERE NL* typeConstraint (NL* COMMA NL* typeConstraint)*
//     ;

// typeConstraint
//     : annotation* simpleIdentifier NL* COLON NL* type
//     ;

// // SECTION: s

// classMemberDeclarations
//     : (classMemberDeclaration semis?)*
//     ;
    .convert(
        filter = byDescription("classMemberDeclarations")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("classMemberDeclaration"))
    }

// classMemberDeclaration
//     : declaration
//     | companionObject
//     | anonymousInitializer
//     | secondaryConstructor
//     ;
    .convert(
        filter = byDescription("classMemberDeclaration")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// anonymousInitializer
//     : INIT NL* block
//     ;

// companionObject
//     : modifiers? COMPANION NL* OBJECT
//     (NL* simpleIdentifier)?
//     (NL* COLON NL* delegationSpecifiers)?
//     (NL* classBody)?
//     ;
    .convert(
        filter = byDescription("companionObject")
    ) { node: AstNode ->
        toKlassDeclaration(node) { ast ->
            astContinue(
                ast.filter(
                    setOf(
                        "classBody",
                    )
                )
            )
        }.toAstList()
    }

// functionValueParameters
//     : LPAREN NL* (functionValueParameter (NL* COMMA NL* functionValueParameter)* (NL* COMMA)?)? NL* RPAREN
//     ;
    .convert(
        filter = byDescription("functionValueParameters")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("functionValueParameter"))
    }

// functionValueParameter
//     : parameterModifiers? parameter (NL* ASSIGNMENT NL* expression)?
//     ;
    .convert(
        filter = byDescription("functionValueParameter")
    ) { node: AstNode ->
        recursiveFlatten(
            node,
            filter = byDescription("parameterModifiers", "parameter", "expression")
        ).flatMap { result ->
            val identifiers = result.filterIsInstance<KlassIdentifier>()

            astContinue(
                KlassDeclaration(
                    keyword = "parameter",
                    identifier = identifiers[0],
                    type = identifiers.drop(1),
                    annotations = result.filterIsInstance<KlassAnnotation>(),
                    modifiers = result.filterIsInstance<KlassModifier>(),
                    expressions = result.expressions()
                )
            )
        }
    }

// functionDeclaration
//     : modifiers?
//     FUN (NL* typeParameters)? (NL* receiverType NL* DOT)? NL* simpleIdentifier
//     NL* functionValueParameters
//     (NL* COLON NL* type)?
//     (NL* typeConstraints)?
//     (NL* functionBody)?
//     ;
    .convert(
        filter = byDescription("functionDeclaration")
    ) { node: AstNode ->
        recursiveFlatten(
            node,
            filter = !byDescription("FUN", "COLON")
        ).flatMap { result ->
            val identifiers = result.filterIsInstance<KlassIdentifier>()
            val receiverType: List<KlassIdentifier> = result
                .filter("receiverType")
                .filterIsInstance<AstNode>()
                .flatMap(AstNode::children)
                .filterIsInstance<KlassIdentifier>()

            astContinue(
                KlassDeclaration(
                    keyword = "fun",
                    identifier = identifiers[0],
                    type = identifiers.drop(1),
                    receiverType = receiverType,
                    annotations = result.filterIsInstance<KlassAnnotation>(),
                    modifiers = result.filterIsInstance<KlassModifier>(),
                    parameter = result.filterIsInstance<KlassDeclaration>(),
                    typeParameters = result.filterIsInstance<KlassTypeParameter>()
                )
            )
        }
    }

// functionBody
//     : block
//     | ASSIGNMENT NL* expression
//     ;
    .convert(
        filter = byDescription("functionBody")
    ) { _: Ast ->
        astDrop()
    }

// variableDeclaration
//     : annotation* NL* simpleIdentifier (NL* COLON NL* type)?
//     ;
    .convert(
        filter = byDescription("variableDeclaration")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// multiVariableDeclaration
//     : LPAREN NL* variableDeclaration (NL* COMMA NL* variableDeclaration)* (NL* COMMA)? NL* RPAREN
//     ;

// propertyDeclaration
//     : modifiers? (VAL | VAR)
//     (NL* typeParameters)?
//     (NL* receiverType NL* DOT)?
//     (NL* (multiVariableDeclaration | variableDeclaration))
//     (NL* typeConstraints)?
//     (NL* (ASSIGNMENT NL* expression | propertyDelegate))?
//     (NL+ SEMICOLON)? NL* (getter? (NL* semi? setter)? | setter? (NL* semi? getter)?)
//     ;
    .convert(
        filter = byDescription("propertyDeclaration")
    ) { node: AstNode ->
        toKlassDeclaration(node) { ast ->
            astContinue(ast.expressions())
        }.toAstList()
    }

// propertyDelegate
//     : BY NL* expression
//     ;
    .convert(
        filter = byDescription("propertyDelegate")
    ) { node: AstNode ->
        recursiveChildren(node, filter = byDescription("expression", "BY"))
    }

// getter
//     : modifiers? GET
//     | modifiers? GET NL* LPAREN NL* RPAREN (NL* COLON NL* type)? NL* functionBody
//     ;

// setter
//     : modifiers? SET
//     | modifiers? SET NL* LPAREN NL* parameterWithOptionalType (NL* COMMA)? NL* RPAREN (NL* COLON NL* type)? NL* functionBody
//     ;

// parametersWithOptionalType
//     : LPAREN NL* (parameterWithOptionalType (NL* COMMA NL* parameterWithOptionalType)* (NL* COMMA)?)? NL* RPAREN
//     ;

// parameterWithOptionalType
//     : parameterModifiers? simpleIdentifier NL* (COLON NL* type)?
//     ;

// parameter
//     : simpleIdentifier NL* COLON NL* type
//     ;
    .convert(
        filter = byDescription("parameter")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("simpleIdentifier", "type"))
    }

// objectDeclaration
//     : modifiers? OBJECT
//     NL* simpleIdentifier
//     (NL* COLON NL* delegationSpecifiers)?
//     (NL* classBody)?
//     ;
    .convert(
        filter = byDescription("objectDeclaration")
    ) { node: AstNode ->
        toKlassDeclaration(node) { ast ->
            astContinue(
                ast.filter(
                    setOf(
                        "classBody",
                    )
                )
            )
        }.toAstList()
    }

// secondaryConstructor
//     : modifiers? CONSTRUCTOR NL* functionValueParameters (NL* COLON NL* constructorDelegationCall)? NL* block?
//     ;

// constructorDelegationCall
//     : THIS NL* valueArguments
//     | SUPER NL* valueArguments
//     ;

// // SECTION: enumClasses

// enumClassBody
//     : LCURL NL* enumEntries? (NL* SEMICOLON NL* classMemberDeclarations)? NL* RCURL
//     ;

// enumEntries
//     : enumEntry (NL* COMMA NL* enumEntry)* NL* COMMA?
//     ;

// enumEntry
//     : (modifiers NL*)? simpleIdentifier (NL* valueArguments)? (NL* classBody)?
//     ;

// // SECTION: types

// type
//     : typeModifiers?
//     ( parenthesizedType
//     | nullableType
//     | typeReference
//     | functionType)
//     ;
    .convert(
        filter = byDescription("type")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// typeReference
//     : userType
//     | DYNAMIC
//     ;
    .convert(
        filter = byDescription("typeReference")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// nullableType
//     : (typeReference | parenthesizedType) NL* quest+
//     ;
    .convert(
        filter = byDescription("nullableType")
    ) { node: AstNode ->
        recursiveFlatten(
            node, filter = byDescription(
                "typeReference",
                "parenthesizedType",
            )
        ).map { astList ->
            astList.map { ast ->
                if (ast is KlassIdentifier) {
                    ast.copy(nullable = true)
                } else {
                    ast
                }
            }
        }
    }

// quest
//     : QUEST_NO_WS
//     | QUEST_WS
//     ;

// userType
//     : simpleUserType (NL* DOT NL* simpleUserType)*
//     ;
    .convert(
        filter = byDescription("userType")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("simpleUserType"))
    }

// simpleUserType
//     : simpleIdentifier (NL* typeArguments)?
//     ;
    .convert(
        filter = byDescription("simpleUserType")
    ) { node: AstNode ->
        recursiveFlatten(node).flatMap { list ->
            val identifier = list.reduce { identifier, parameter ->
                when {
                    identifier is KlassIdentifier && parameter is KlassIdentifier ->
                        identifier.parameterizedBy(parameter)
                    else ->
                        identifier
                }
            }
            astContinue(identifier)
        }
    }

// typeProjection
//     : typeProjectionModifiers? type | MULT
//     ;
    .convert(
        filter = byDescription("typeProjection")
    ) { node: AstNode ->
        val descriptions = node.children.descriptions()
        if (descriptions == listOf("MULT")) {
            astContinue(starProjection)
        } else {
            recursiveFlatten(node).flatMap { list ->
                list.fold(
                    astSuccess(
                        Pair(emptyList<KlassModifier>(), emptyList<KlassIdentifier>())
                    )
                ) { result, right ->
                    when {
                        result is AstSuccess && result.success.second.isEmpty() ->
                            when (right) {
                                is KlassIdentifier ->
                                    astSuccess(
                                        result.success.copy(
                                            second = listOf(right)
                                        )
                                    )
                                is KlassModifier ->
                                    astSuccess(
                                        result.success.copy(
                                            first = result.success.first + right
                                        )
                                    )
                                else ->
                                    astFailure("found unsupported ast node '$right' in typeProjection!")
                            }

                        result is AstSuccess && result.success.second.size == 1 -> {
                            val left = result.success.second.first()
                            when {
                                right is KlassIdentifier -> {
                                    val identifier = listOf(
                                        left.identifier,
                                        right.identifier
                                    ).joinToString(".")
                                    astSuccess(
                                        result.success.copy(
                                            second = listOf(
                                                left.copy(identifier = identifier)
                                            )
                                        )
                                    )
                                }
                                right.description == "quest" ->
                                    astSuccess(
                                        result.success.copy(
                                            second = listOf(left.copy(nullable = true))
                                        )
                                    )
                                else ->
                                    astFailure("found unsupported ast node '$right' in typeProjection!")
                            }
                        }
                        else ->
                            result
                    }
                }.flatMap { (modifiers, identifiers) ->
                    when {
                        identifiers.isEmpty() ->
                            astFailure("missing identifier in typeProjection!")
                        identifiers.size != 1 ->
                            astFailure("expected exactly one identifier in typeProjection!")
                        modifiers.isEmpty() ->
                            astSuccess(listOf(identifiers.first()))
                        else ->
                            astSuccess(listOf(identifiers.first().withModifiers(modifiers)))
                    }
                }
            }
        }
    }

// typeProjectionModifiers
//     : typeProjectionModifier+
//     ;
    .convert(
        filter = byDescription("typeProjectionModifiers")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// typeProjectionModifier
//     : varianceModifier NL*
//     | annotation
//     ;
    .convert(
        filter = byDescription("typeProjectionModifier")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// functionType
//     : (receiverType NL* DOT NL*)? functionTypeParameters NL* ARROW NL* type
//     ;

// functionTypeParameters
//     : LPAREN NL* (parameter | type)? (NL* COMMA NL* (parameter | type))* (NL* COMMA)? NL* RPAREN
//     ;

// parenthesizedType
//     : LPAREN NL* type NL* RPAREN
//     ;

// receiverType
//     : typeModifiers?
//     ( parenthesizedType
//     | nullableType
//     | typeReference)
//     ;
    .convert(
        filter = byDescription("receiverType")
    ) { node: AstNode ->
        recursiveChildren(node)
    }

// parenthesizedUserType
//     : LPAREN NL* userType NL* RPAREN
//     | LPAREN NL* parenthesizedUserType NL* RPAREN
//     ;

// // SECTION: statements

// statements
//     : (statement (semis statement)*)? semis?
//     ;

// statement
//     : (label | annotation)*
//     ( declaration
//     | assignment
//     | loopStatement
//     | expression)
//     ;

// label
//     : simpleIdentifier (AT_NO_WS | AT_POST_WS) NL*
//     ;

// controlStructureBody
//     : block
//     | statement
//     ;

// block
//     : LCURL NL* statements NL* RCURL
//     ;

// loopStatement
//     : forStatement
//     | whileStatement
//     | doWhileStatement
//     ;

// forStatement
//     : FOR NL* LPAREN annotation* (variableDeclaration | multiVariableDeclaration) IN expression RPAREN NL* controlStructureBody?
//     ;

// whileStatement
//     : WHILE NL* LPAREN expression RPAREN NL* controlStructureBody
//     | WHILE NL* LPAREN expression RPAREN NL* SEMICOLON
//     ;

// doWhileStatement
//     : DO NL* controlStructureBody? NL* WHILE NL* LPAREN expression RPAREN
//     ;

// assignment
//     : directlyAssignableExpression ASSIGNMENT NL* expression
//     | assignableExpression assignmentAndOperator NL* expression
//     ;

// semi
//     : (SEMICOLON | NL) NL*
//     | EOF;
    .convert(
        filter = byDescription("semi")
    ) { _: AstNode ->
        astDrop()
    }

// semis
//     : (SEMICOLON | NL)+
//     | EOF
//     ;
    .convert(
        filter = byDescription("semis")
    ) { _: AstNode ->
        astDrop()
    }

// // SECTION: expressions

// expression
//     : disjunction
//     ;
    .convert(
        filter = byDescription("expression")
    ) { node: AstNode ->
        recursiveChildren(node)
    }

// disjunction
//     : conjunction (NL* DISJ NL* conjunction)*
//     ;
    .convert(
        filter = byDescription("disjunction")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("conjunction"))
    }

// conjunction
//     : equality (NL* CONJ NL* equality)*
//     ;
    .convert(
        filter = byDescription("conjunction")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("equality"))
    }

// equality
//     : comparison (equalityOperator NL* comparison)*
//     ;
    .convert(
        filter = byDescription("equality")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("comparison", "equalityOperator"))
    }

// comparison
//     : genericCallLikeComparison (comparisonOperator NL* genericCallLikeComparison)*
//     ;
    .convert(
        filter = byDescription("comparison")
    ) { node: AstNode ->
        recursiveFlattenSingle(
            node,
            filter = byDescription("genericCallLikeComparison", "comparisonOperator")
        )
    }

// genericCallLikeComparison
//    : infixOperation callSuffix*
//    ;
    .convert(
        filter = byDescription("genericCallLikeComparison")
    ) { node: AstNode ->
        recursiveFlattenSingle(node)
    }

// infixOperation
//     : elvisExpression (inOperator NL* elvisExpression | isOperator NL* type)*
//     ;
    .convert(
        filter = byDescription("infixOperation")
    ) { node: AstNode ->
        recursiveFlattenSingle(
            node,
            filter = byDescription("elvisExpression", "inOperator", "isOperator", "type")
        )
    }

// elvisExpression
//     : infixFunctionCall (NL* elvis NL* infixFunctionCall)*
//     ;
    .convert(
        filter = byDescription("elvisExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("infixFunctionCall"))
    }

// elvis
//     : QUEST_NO_WS COLON
//     ;

// infixFunctionCall
//     : rangeExpression (simpleIdentifier NL* rangeExpression)*
//     ;
    .convert(
        filter = byDescription("infixFunctionCall")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("rangeExpression", "simpleIdentifier"))
    }

// rangeExpression
//     : additiveExpression (RANGE NL* additiveExpression)*
//     ;
    .convert(
        filter = byDescription("rangeExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("additiveExpression"))
    }

// additiveExpression
//     : multiplicativeExpression (additiveOperator NL* multiplicativeExpression)*
//     ;
    .convert(
        filter = byDescription("additiveExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("multiplicativeExpression", "additiveOperator"))
    }

// multiplicativeExpression
//     : asExpression (multiplicativeOperator NL* asExpression)*
//     ;
    .convert(
        filter = byDescription("multiplicativeExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("asExpression", "multiplicativeOperator"))
    }

// asExpression
//     : prefixUnaryExpression (NL* asOperator NL* type)*
//     ;
    .convert(
        filter = byDescription("asExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("prefixUnaryExpression", "type"))
    }

// prefixUnaryExpression
//     : unaryPrefix* postfixUnaryExpression
//     ;
    .convert(
        filter = byDescription("prefixUnaryExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("unaryPrefix", "postfixUnaryExpression"))
    }

// unaryPrefix
//     : annotation
//     | label
//     | prefixUnaryOperator NL*
//     ;

// postfixUnaryExpression
//     : primaryExpression
//     | primaryExpression postfixUnarySuffix+
//     ;
    .convert(
        filter = byDescription("postfixUnaryExpression")
    ) { node: AstNode ->
        recursiveFlatten(node).flatMap { nodes ->
            when (val identifier = nodes.getOrNull(0) as? KlassIdentifier) {
                is KlassIdentifier -> astContinue(
                    if (nodes.all { it is KlassIdentifier }) {
                        val parameter = nodes
                            .drop(1)
                            .filterIsInstance<KlassIdentifier>()
                            .toTypedArray()
                        identifier.parameterizedBy(*parameter)
                    } else {
                        identifier.copy(
                            children = identifier.children + nodes.drop(1)
                        )
                    }
                )
                else ->
                    astContinue(nodes)
            }
        }
    }

// postfixUnarySuffix
//     : postfixUnaryOperator
//     | typeArguments
//     | callSuffix
//     | indexingSuffix
//     | navigationSuffix
//     ;
    .convert(
        filter = byDescription("postfixUnarySuffix")
    ) { node: AstNode ->
        recursiveChildren(node)
    }

// directlyAssignableExpression
//     : postfixUnaryExpression assignableSuffix
//     | simpleIdentifier
//     | parenthesizedDirectlyAssignableExpression
//     ;

// parenthesizedDirectlyAssignableExpression
//     : LPAREN NL* directlyAssignableExpression NL* RPAREN
//     ;
    .convert(
        filter = byDescription("parenthesizedDirectlyAssignableExpression")
    ) { node: AstNode ->
        node.filterChildren(filter = byDescription("directlyAssignableExpression"))
            .flatMap { filtered ->
                recursiveChildren(filtered)
            }
    }

// assignableExpression
//     : prefixUnaryExpression | parenthesizedAssignableExpression
//     ;

// parenthesizedAssignableExpression
//     : LPAREN NL* assignableExpression NL* RPAREN
//     ;
    .convert(
        filter = byDescription("parenthesizedAssignableExpression")
    ) { node: AstNode ->
        node.filterChildren(filter = byDescription("assignableExpression"))
            .flatMap { filtered ->
                recursiveChildren(filtered)
            }
    }

// assignableSuffix
//     : typeArguments
//     | indexingSuffix
//     | navigationSuffix
//     ;

// indexingSuffix
//     : LSQUARE NL* expression (NL* COMMA NL* expression)* (NL* COMMA)? NL* RSQUARE
//     ;

// navigationSuffix
//     : NL* memberAccessOperator NL* (simpleIdentifier | parenthesizedExpression | CLASS)
//     ;

// callSuffix
//     : typeArguments? valueArguments? annotatedLambda
//     | typeArguments? valueArguments
//     ;
    .convert(
        filter = byDescription("callSuffix")
    ) { node: AstNode ->
        recursiveChildren(node)
    }

// annotatedLambda
//     : annotation* label? NL* lambdaLiteral
//     ;

// typeArguments
//     : LANGLE NL* typeProjection (NL* COMMA NL* typeProjection)* (NL* COMMA)? NL* RANGLE
//     ;
    .convert(
        filter = byDescription("typeArguments")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("typeProjection"))
    }

// valueArguments
//     : LPAREN NL* RPAREN
//     | LPAREN NL* valueArgument (NL* COMMA NL* valueArgument)* (NL* COMMA)? NL* RPAREN
//     ;
    .convert(
        filter = byDescription("valueArguments")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("valueArgument"))
    }

// valueArgument
//     : annotation? NL* (simpleIdentifier NL* ASSIGNMENT NL*)? MULT? NL* expression
//     ;
    .convert(
        filter = byDescription("valueArgument")
    ) { node: AstNode ->
        toKlassDeclaration(node) { ast ->
            recursive(ast.expressions())
        }.toAstList()
    }

// primaryExpression
//     : parenthesizedExpression
//     | simpleIdentifier
//     | literalConstant
//     | stringLiteral
//     | callableReference
//     | functionLiteral
//     | objectLiteral
//     | collectionLiteral
//     | thisExpression
//     | superExpression
//     | ifExpression
//     | whenExpression
//     | tryExpression
//     | jumpExpression
//     ;
    .convert(
        filter = byDescription("primaryExpression")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// parenthesizedExpression
//     : LPAREN NL* expression NL* RPAREN
//     ;

// collectionLiteral
//     : LSQUARE NL* expression (NL* COMMA NL* expression)* (NL* COMMA)? NL* RSQUARE
//     | LSQUARE NL* RSQUARE
//     ;

// literalConstant
//     : BooleanLiteral
//     | IntegerLiteral
//     | HexLiteral
//     | BinLiteral
//     | CharacterLiteral
//     | RealLiteral
//     | NullLiteral
//     | LongLiteral
//     | UnsignedLiteral
//     ;

// stringLiteral
//     : lineStringLiteral
//     | multiLineStringLiteral
//     ;
    .convert(
        filter = byDescription("stringLiteral")
    ) { node: AstNode ->
        recursiveFlatten(node).flatMap { result ->
            astContinue(
                KlassString(
                    children = result.map { ast ->
                        if (ast is StringComponent) {
                            ast
                        } else {
                            ast.asStringComponent()
                        }
                    },
                )
            )
        }
    }

// lineStringLiteral
//     : QUOTE_OPEN (lineStringContent | lineStringExpression)* QUOTE_CLOSE
//     ;
    .convert(
        filter = byDescription("lineStringLiteral")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("lineStringContent", "lineStringExpression"))
    }

// multiLineStringLiteral
//     : TRIPLE_QUOTE_OPEN (multiLineStringContent | multiLineStringExpression | MultiLineStringQuote)* TRIPLE_QUOTE_CLOSE
//     ;
    .convert(
        filter = byDescription("multiLineStringLiteral")
    ) { node: AstNode ->
        recursiveFlatten(
            node,
            filter = byDescription(
                "multiLineStringContent",
                "multiLineStringExpression",
                "MultiLineStringQuote"
            )
        )
    }

// lineStringContent
//     : LineStrText
//     | LineStrEscapedChar
//     | LineStrRef
//     ;
    .convert(
        filter = byDescription("lineStringContent")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// lineStringExpression
//     : LineStrExprStart expression RCURL
//     ;
    .convert(
        filter = byDescription("lineStringExpression")
    ) { node: AstNode ->
        recursiveFlattenSingle(node, filter = byDescription("expression"))
    }

// multiLineStringContent
//     : MultiLineStrText
//     | MultiLineStringQuote
//     | MultiLineStrRef
//     ;
    .convert(
        filter = byDescription("multiLineStringContent")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// multiLineStringExpression
//     : MultiLineStrExprStart NL* expression NL* RCURL
//     ;
    .convert(
        filter = byDescription("multiLineStringExpression")
    ) { node: AstNode ->
        node.filterChildren(filter = byDescription("expression"))
            .flatMap { multiLineStringExpression ->
                convertKotlinStringComponent(multiLineStringExpression)
            }
    }

// lambdaLiteral
//     : LCURL NL* statements NL* RCURL
//     | LCURL NL* lambdaParameters? NL* ARROW NL* statements NL* RCURL
//     ;

// lambdaParameters
//     : lambdaParameter (NL* COMMA NL* lambdaParameter)* (NL* COMMA)?
//     ;

// lambdaParameter
//     : variableDeclaration
//     | multiVariableDeclaration (NL* COLON NL* type)?
//     ;

// anonymousFunction
//     : FUN
//     (NL* type NL* DOT)?
//     NL* parametersWithOptionalType
//     (NL* COLON NL* type)?
//     (NL* typeConstraints)?
//     (NL* functionBody)?
//     ;

// functionLiteral
//     : lambdaLiteral
//     | anonymousFunction
//     ;

// objectLiteral
//     : OBJECT NL* COLON NL* delegationSpecifiers NL* classBody
//     | OBJECT NL* classBody
//     ;

// thisExpression
//     : THIS
//     | THIS_AT
//     ;

// superExpression
//     : SUPER (LANGLE NL* type NL* RANGLE)? (AT_NO_WS simpleIdentifier)?
//     | SUPER_AT
//     ;

// ifExpression
//     : IF NL* LPAREN NL* expression NL* RPAREN NL* (controlStructureBody | SEMICOLON)
//     | IF NL* LPAREN NL* expression NL* RPAREN NL* controlStructureBody? NL* SEMICOLON? NL* ELSE NL* (controlStructureBody | SEMICOLON)
//     ;

// whenSubject
//     : LPAREN (annotation* NL* VAL NL* variableDeclaration NL* ASSIGNMENT NL*)? expression RPAREN
//     ;

// whenExpression
//     : WHEN NL* whenSubject? NL* LCURL NL* (whenEntry NL*)* NL* RCURL
//     ;

// whenEntry
//     : whenCondition (NL* COMMA NL* whenCondition)* (NL* COMMA)? NL* ARROW NL* controlStructureBody semi?
//     | ELSE NL* ARROW NL* controlStructureBody semi?
//     ;

// whenCondition
//     : expression
//     | rangeTest
//     | typeTest
//     ;

// rangeTest
//     : inOperator NL* expression
//     ;

// typeTest
//     : isOperator NL* type
//     ;

// tryExpression
//     : TRY NL* block ((NL* catchBlock)+ (NL* finallyBlock)? | NL* finallyBlock)
//     ;

// catchBlock
//     : CATCH NL* LPAREN annotation* simpleIdentifier COLON type (NL* COMMA)? RPAREN NL* block
//     ;

// finallyBlock
//     : FINALLY NL* block
//     ;

// jumpExpression
//     : THROW NL* expression
//     | (RETURN | RETURN_AT) expression?
//     | CONTINUE | CONTINUE_AT
//     | BREAK | BREAK_AT
//     ;

// callableReference
//     : (receiverType? NL* COLONCOLON NL* (simpleIdentifier | CLASS))
//     ;

// assignmentAndOperator
//     : ADD_ASSIGNMENT
//     | SUB_ASSIGNMENT
//     | MULT_ASSIGNMENT
//     | DIV_ASSIGNMENT
//     | MOD_ASSIGNMENT
//     ;

// equalityOperator
//     : EXCL_EQ
//     | EXCL_EQEQ
//     | EQEQ
//     | EQEQEQ
//     ;

// comparisonOperator
//     : LANGLE
//     | RANGLE
//     | LE
//     | GE
//     ;

// inOperator
//     : IN | NOT_IN
//     ;

// isOperator
//     : IS | NOT_IS
//     ;

// additiveOperator
//     : ADD | SUB
//     ;

// multiplicativeOperator
//     : MULT
//     | DIV
//     | MOD
//     ;

// asOperator
//     : AS
//     | AS_SAFE
//     ;

// prefixUnaryOperator
//     : INCR
//     | DECR
//     | SUB
//     | ADD
//     | excl
//     ;

// postfixUnaryOperator
//     : INCR
//     | DECR
//     | EXCL_NO_WS excl
//     ;

// excl
//     : EXCL_NO_WS
//     | EXCL_WS
//     ;

// memberAccessOperator
//     : DOT | safeNav | COLONCOLON
//     ;

// safeNav
//     : QUEST_NO_WS DOT
//     ;

// // SECTION: modifiers

// modifiers
//     : (annotation | modifier)+
//     ;
    .convert(
        filter = byDescription("modifiers")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// parameterModifiers
//     : (annotation | parameterModifier)+
//     ;
    .convert(
        filter = byDescription("parameterModifiers")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// modifier
//     : (classModifier
//     | memberModifier
//     | visibilityModifier
//     | functionModifier
//     | propertyModifier
//     | inheritanceModifier
//     | parameterModifier
//     | platformModifier) NL*
//     ;
    .convert(
        filter = byDescription("modifier")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// typeModifiers
//     : typeModifier+
//     ;
    .convert(
        filter = byDescription("typeModifiers")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// typeModifier
//     : annotation | SUSPEND NL*
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("typeModifier")
    )

// classModifier
//     : ENUM
//     | SEALED
//     | ANNOTATION
//     | DATA
//     | INNER
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("classModifier")
    )

// memberModifier
//     : OVERRIDE
//     | LATEINIT
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("memberModifier")
    )

// visibilityModifier
//     : PUBLIC
//     | PRIVATE
//     | INTERNAL
//     | PROTECTED
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("visibilityModifier")
    )

// varianceModifier
//     : IN
//     | OUT
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("varianceModifier")
    )

// typeParameterModifiers
//     : typeParameterModifier+
//     ;
    .convert(
        filter = byDescription("typeParameterModifiers")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// typeParameterModifier
//     : reificationModifier NL*
//     | varianceModifier NL*
//     | annotation
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("typeParameterModifier")
    )

// functionModifier
//     : TAILREC
//     | OPERATOR
//     | INFIX
//     | INLINE
//     | EXTERNAL
//     | SUSPEND
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("functionModifier")
    )

// propertyModifier
//     : CONST
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("propertyModifier")
    )

// inheritanceModifier
//     : ABSTRACT
//     | FINAL
//     | OPEN
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("inheritanceModifier")
    )

// parameterModifier
//     : VARARG
//     | NOINLINE
//     | CROSSINLINE
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("parameterModifier")
    )

// reificationModifier
//     : REIFIED
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("reificationModifier")
    )

// platformModifier
//     : EXPECT
//     | ACTUAL
//     ;
    .convertKotlinKlassModifier(
        filter = byDescription("platformModifier")
    )

// // SECTION: annotations

// annotation
//     : (singleAnnotation | multiAnnotation) NL*
//     ;
    .convert(
        filter = byDescription("annotation")
    ) { node: AstNode ->
        recursiveFlatten(node).flatMap { result ->
            astContinue(
                KlassAnnotation(
                    identifier = result.filterIsInstance<KlassIdentifier>(),
                    arguments = result.filterIsInstance<KlassDeclaration>()
                )
            )
        }
    }

// singleAnnotation
//     : annotationUseSiteTarget NL* unescapedAnnotation
//     | (AT_NO_WS | AT_PRE_WS) unescapedAnnotation
//     ;
    .convert(
        filter = byDescription("singleAnnotation")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("unescapedAnnotation"))
    }

// multiAnnotation
//     : annotationUseSiteTarget NL* LSQUARE unescapedAnnotation+ RSQUARE
//     | (AT_NO_WS | AT_PRE_WS) LSQUARE unescapedAnnotation+ RSQUARE
//     ;

// annotationUseSiteTarget
//     : (AT_NO_WS | AT_PRE_WS) (FIELD | PROPERTY | GET | SET | RECEIVER | PARAM | SETPARAM | DELEGATE) NL* COLON
//     ;

// unescapedAnnotation
//     : constructorInvocation
//     | userType
//     ;
    .convert(
        filter = byDescription("unescapedAnnotation")
    ) { node: AstNode ->
        recursiveFlatten(node)
    }

// // SECTION: identifiers

// simpleIdentifier: Identifier
//     | ABSTRACT
//     | ANNOTATION
//     | BY
//     | CATCH
//     | COMPANION
//     | CONSTRUCTOR
//     | CROSSINLINE
//     | DATA
//     | DYNAMIC
//     | ENUM
//     | EXTERNAL
//     | FINAL
//     | FINALLY
//     | GET
//     | IMPORT
//     | INFIX
//     | INIT
//     | INLINE
//     | INNER
//     | INTERNAL
//     | LATEINIT
//     | NOINLINE
//     | OPEN
//     | OPERATOR
//     | OUT
//     | OVERRIDE
//     | PRIVATE
//     | PROTECTED
//     | PUBLIC
//     | REIFIED
//     | SEALED
//     | TAILREC
//     | SET
//     | VARARG
//     | WHERE
//     | FIELD
//     | PROPERTY
//     | RECEIVER
//     | PARAM
//     | SETPARAM
//     | DELEGATE
//     | FILE
//     | EXPECT
//     | ACTUAL
//     | CONST
//     | SUSPEND
//     ;
    .convert(
        filter = byDescription("simpleIdentifier")
    ) { node: AstNode ->
        if (node.children.size == 1) {
            val terminal = node.children.first() as AstTerminal?
            if (terminal != null) {
                astContinue(KlassIdentifier(terminal.text))
            } else {
                astFailure("expected AstTerminal in simpleIdentifier")
            }
        } else {
            astFailure("expected AstTerminal in simpleIdentifier")
        }
    }

// identifier
//     : simpleIdentifier (NL* DOT simpleIdentifier)*
//     ;
    .convert(
        filter = byDescription("identifier")
    ) { node: AstNode ->
        recursiveFlatten(node, filter = byDescription("simpleIdentifier"))
    }

///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////
// /**
//  * Kotlin lexical grammar in ANTLR4 notation
//  */
//
// lexer grammar KotlinLexer;
//
// import UnicodeClasses;
//
// // SECTION: lexicalGeneral
//
// channels { COMMENT }

// ShebangLine
//     : '#!' ~[\r\n]*
//     ;

// DelimitedComment
//     : '/*' ( DelimitedComment | . )*? '*/'
//       -> channel(COMMENT)
//     ;
    .convertKotlinKlassComment(
        filter = byDescription("DelimitedComment")
    )

// LineComment
//     : '//' ~[\r\n]*
//       -> channel(COMMENT)
//     ;
    .convertKotlinKlassComment(
        filter = byDescription("LineComment")
    )

// WS
//     : [\u0020\u0009\u000C]
//       -> channel(HIDDEN)
//     ;

// NL: '\n' | '\r' '\n'?;
    .convert(
        filter = byDescription("NL")
    ) { _: AstTerminal ->
        astDrop()
    }

// fragment Hidden: DelimitedComment | LineComment | WS;

// // SECTION: separatorsAndOperations

// RESERVED: '...';
// DOT: '.';
// COMMA: ',';
// LPAREN: '(' -> pushMode(Inside);
// RPAREN: ')';
// LSQUARE: '[' -> pushMode(Inside);
// RSQUARE: ']';
// LCURL: '{' -> pushMode(DEFAULT_MODE);
// /*
//  * When using another programming language (not Java) to generate a parser,
//  * please replace this code with the corresponding code of a programming language you are using.
//  */
// RCURL: '}' -> popMode;
// MULT: '*';
// MOD: '%';
// DIV: '/';
// ADD: '+';
// SUB: '-';
// INCR: '++';
// DECR: '--';
// CONJ: '&&';
// DISJ: '||';
// EXCL_WS: '!' Hidden;
// EXCL_NO_WS: '!';
// COLON: ':';
// SEMICOLON: ';';
// ASSIGNMENT: '=';
// ADD_ASSIGNMENT: '+=';
// SUB_ASSIGNMENT: '-=';
// MULT_ASSIGNMENT: '*=';
// DIV_ASSIGNMENT: '/=';
// MOD_ASSIGNMENT: '%=';
// ARROW: '->';
// DOUBLE_ARROW: '=>';
// RANGE: '..';
// COLONCOLON: '::';
// DOUBLE_SEMICOLON: ';;';
// HASH: '#';
// AT_NO_WS: '@';
// AT_POST_WS: '@' (Hidden | NL);
// AT_PRE_WS: (Hidden | NL) '@' ;
// AT_BOTH_WS: (Hidden | NL) '@' (Hidden | NL);
// QUEST_WS: '?' Hidden;
// QUEST_NO_WS: '?';
// LANGLE: '<';
// RANGLE: '>';
// LE: '<=';
// GE: '>=';
// EXCL_EQ: '!=';
// EXCL_EQEQ: '!==';
// AS_SAFE: 'as?';
// EQEQ: '==';
// EQEQEQ: '===';
// SINGLE_QUOTE: '\'';

// // SECTION: keywords

// RETURN_AT: 'return@' Identifier;
// CONTINUE_AT: 'continue@' Identifier;
// BREAK_AT: 'break@' Identifier;

// THIS_AT: 'this@' Identifier;
// SUPER_AT: 'super@' Identifier;

// FILE: 'file';
// FIELD: 'field';
// PROPERTY: 'property';
// GET: 'get';
// SET: 'set';
// RECEIVER: 'receiver';
// PARAM: 'param';
// SETPARAM: 'setparam';
// DELEGATE: 'delegate';

// PACKAGE: 'package';
// IMPORT: 'import';
// CLASS: 'class';
// INTERFACE: 'interface';
// FUN: 'fun';
// OBJECT: 'object';
// VAL: 'val';
// VAR: 'var';
// TYPE_ALIAS: 'typealias';
// CONSTRUCTOR: 'constructor';
// BY: 'by';
// COMPANION: 'companion';
// INIT: 'init';
// THIS: 'this';
// SUPER: 'super';
// TYPEOF: 'typeof';
// WHERE: 'where';
// IF: 'if';
// ELSE: 'else';
// WHEN: 'when';
// TRY: 'try';
// CATCH: 'catch';
// FINALLY: 'finally';
// FOR: 'for';
// DO: 'do';
// WHILE: 'while';
// THROW: 'throw';
// RETURN: 'return';
// CONTINUE: 'continue';
// BREAK: 'break';
// AS: 'as';
// IS: 'is';
// IN: 'in';
// NOT_IS: '!is' (Hidden | NL);
// NOT_IN: '!in' (Hidden | NL);
// OUT: 'out';
// DYNAMIC: 'dynamic';

// // SECTION: lexicalModifiers

// PUBLIC: 'public';
// PRIVATE: 'private';
// PROTECTED: 'protected';
// INTERNAL: 'internal';
// ENUM: 'enum';
// SEALED: 'sealed';
// ANNOTATION: 'annotation';
// DATA: 'data';
// INNER: 'inner';
// TAILREC: 'tailrec';
// OPERATOR: 'operator';
// INLINE: 'inline';
// INFIX: 'infix';
// EXTERNAL: 'external';
// SUSPEND: 'suspend';
// OVERRIDE: 'override';
// ABSTRACT: 'abstract';
// FINAL: 'final';
// OPEN: 'open';
// CONST: 'const';
// LATEINIT: 'lateinit';
// VARARG: 'vararg';
// NOINLINE: 'noinline';
// CROSSINLINE: 'crossinline';
// REIFIED: 'reified';
// EXPECT: 'expect';
// ACTUAL: 'actual';

// // SECTION: literals

// fragment DecDigit: '0'..'9';
// fragment DecDigitNoZero: '1'..'9';
// fragment DecDigitOrSeparator: DecDigit | '_';

// fragment DecDigits
//     : DecDigit DecDigitOrSeparator* DecDigit
//     | DecDigit
//     ;

// fragment DoubleExponent: [eE] [+-]? DecDigits;

// RealLiteral
//     : FloatLiteral
//     | DoubleLiteral
//     ;

// FloatLiteral
//     : DoubleLiteral [fF]
//     | DecDigits [fF]
//     ;

// DoubleLiteral
//     : DecDigits? '.' DecDigits DoubleExponent?
//     | DecDigits DoubleExponent
//     ;

// IntegerLiteral
//     : DecDigitNoZero DecDigitOrSeparator* DecDigit
//     | DecDigit
//     ;

// fragment HexDigit: [0-9a-fA-F];
// fragment HexDigitOrSeparator: HexDigit | '_';

// HexLiteral
//     : '0' [xX] HexDigit HexDigitOrSeparator* HexDigit
//     | '0' [xX] HexDigit
//     ;

// fragment BinDigit: [01];
// fragment BinDigitOrSeparator: BinDigit | '_';

// BinLiteral
//     : '0' [bB] BinDigit BinDigitOrSeparator* BinDigit
//     | '0' [bB] BinDigit
//     ;

// UnsignedLiteral
//     : (IntegerLiteral | HexLiteral | BinLiteral) [uU] [lL]?
//     ;

// LongLiteral
//     : (IntegerLiteral | HexLiteral | BinLiteral) [lL]
//     ;

// BooleanLiteral: 'true'| 'false';

// NullLiteral: 'null';

// CharacterLiteral
//     : '\'' (EscapeSeq | ~[\n\r'\\]) '\''
//     ;

// // SECTION: lexicalIdentifiers

// fragment UnicodeDigit: UNICODE_CLASS_ND;

// Identifier
//     : (Letter | '_') (Letter | '_' | UnicodeDigit)*
//     | '`' ~([\r\n] | '`')+ '`'
//     ;

// IdentifierOrSoftKey
//     : Identifier
//     /* Soft keywords */
//     | ABSTRACT
//     | ANNOTATION
//     | BY
//     | CATCH
//     | COMPANION
//     | CONSTRUCTOR
//     | CROSSINLINE
//     | DATA
//     | DYNAMIC
//     | ENUM
//     | EXTERNAL
//     | FINAL
//     | FINALLY
//     | IMPORT
//     | INFIX
//     | INIT
//     | INLINE
//     | INNER
//     | INTERNAL
//     | LATEINIT
//     | NOINLINE
//     | OPEN
//     | OPERATOR
//     | OUT
//     | OVERRIDE
//     | PRIVATE
//     | PROTECTED
//     | PUBLIC
//     | REIFIED
//     | SEALED
//     | TAILREC
//     | VARARG
//     | WHERE
//     | GET
//     | SET
//     | FIELD
//     | PROPERTY
//     | RECEIVER
//     | PARAM
//     | SETPARAM
//     | DELEGATE
//     | FILE
//     | EXPECT
//     | ACTUAL
//     /* Strong keywords */
//     | CONST
//     | SUSPEND
//     ;

// FieldIdentifier
//     : '$' IdentifierOrSoftKey
//     ;

// fragment UniCharacterLiteral
//     : '\\' 'u' HexDigit HexDigit HexDigit HexDigit
//     ;

// fragment EscapedIdentifier
//     : '\\' ('t' | 'b' | 'r' | 'n' | '\'' | '"' | '\\' | '$')
//     ;

// fragment EscapeSeq
//     : UniCharacterLiteral
//     | EscapedIdentifier
//     ;

// // SECTION: characters

// fragment Letter
//     : UNICODE_CLASS_LL
//     | UNICODE_CLASS_LM
//     | UNICODE_CLASS_LO
//     | UNICODE_CLASS_LT
//     | UNICODE_CLASS_LU
//     | UNICODE_CLASS_NL
//     ;

// // SECTION: strings

// QUOTE_OPEN: '"' -> pushMode(LineString);

// TRIPLE_QUOTE_OPEN: '"""' -> pushMode(MultiLineString);

// mode LineString;

// QUOTE_CLOSE
//     : '"' -> popMode
//     ;

// LineStrRef
//     : FieldIdentifier
//     ;
    .convert(
        filter = byDescription("LineStrRef")
    ) { terminal: AstTerminal ->
        astContinue(
            KlassIdentifier(
                terminal.text.drop(1),
            ).asStringComponent()
        )
    }

// LineStrText
//     : ~('\\' | '"' | '$')+ | '$'
//     ;
    .convert(
        filter = byDescription("LineStrText")
    ) { terminal: AstTerminal ->
        astContinue(terminal.text.asStringComponentRaw())
    }

// LineStrEscapedChar
//     : EscapedIdentifier
//     | UniCharacterLiteral
//     ;
    .convert(
        filter = byDescription("LineStrEscapedChar")
    ) { terminal: AstTerminal ->
        astContinue(terminal.text.asStringComponentEscape())
    }

// LineStrExprStart
//     : '${' -> pushMode(DEFAULT_MODE)
//     ;

// mode MultiLineString;

// TRIPLE_QUOTE_CLOSE
//     : MultiLineStringQuote? '"""' -> popMode
//     ;

// MultiLineStringQuote
//     : '"'+
//     ;
    .convert(
        filter = byDescription("MultiLineStringQuote")
    ) { terminal: AstTerminal ->
        astContinue(terminal.text.asStringComponentRaw())
    }

// MultiLineStrRef
//     : FieldIdentifier
//     ;
    .convert(
        filter = byDescription("MultiLineStrRef")
    ) { terminal: AstTerminal ->
        astContinue(
            KlassIdentifier(
                terminal.text.drop(1),
            ).asStringComponent()
        )
    }

// MultiLineStrText
//     :  ~('"' | '$')+ | '$'
//     ;
    .convert(
        filter = byDescription("MultiLineStrText")
    ) { terminal: AstTerminal ->
        astContinue(terminal.text.asStringComponentRaw())
    }

// MultiLineStrExprStart
//     : '${' -> pushMode(DEFAULT_MODE)
//     ;

// // SECTION: inside

// mode Inside;

// Inside_RPAREN: RPAREN -> popMode, type(RPAREN);
// Inside_RSQUARE: RSQUARE -> popMode, type(RSQUARE);
// Inside_LPAREN: LPAREN -> pushMode(Inside), type(LPAREN);
// Inside_LSQUARE: LSQUARE -> pushMode(Inside), type(LSQUARE);
// Inside_LCURL: LCURL -> pushMode(DEFAULT_MODE), type(LCURL);
// Inside_RCURL: RCURL -> popMode, type(RCURL);

// Inside_DOT: DOT -> type(DOT);
// Inside_COMMA: COMMA  -> type(COMMA);
// Inside_MULT: MULT -> type(MULT);
// Inside_MOD: MOD  -> type(MOD);
// Inside_DIV: DIV -> type(DIV);
// Inside_ADD: ADD  -> type(ADD);
// Inside_SUB: SUB  -> type(SUB);
// Inside_INCR: INCR  -> type(INCR);
// Inside_DECR: DECR  -> type(DECR);
// Inside_CONJ: CONJ  -> type(CONJ);
// Inside_DISJ: DISJ  -> type(DISJ);
// Inside_EXCL_WS: '!' (Hidden|NL) -> type(EXCL_WS);
// Inside_EXCL_NO_WS: EXCL_NO_WS  -> type(EXCL_NO_WS);
// Inside_COLON: COLON  -> type(COLON);
// Inside_SEMICOLON: SEMICOLON  -> type(SEMICOLON);
// Inside_ASSIGNMENT: ASSIGNMENT  -> type(ASSIGNMENT);
// Inside_ADD_ASSIGNMENT: ADD_ASSIGNMENT  -> type(ADD_ASSIGNMENT);
// Inside_SUB_ASSIGNMENT: SUB_ASSIGNMENT  -> type(SUB_ASSIGNMENT);
// Inside_MULT_ASSIGNMENT: MULT_ASSIGNMENT  -> type(MULT_ASSIGNMENT);
// Inside_DIV_ASSIGNMENT: DIV_ASSIGNMENT  -> type(DIV_ASSIGNMENT);
// Inside_MOD_ASSIGNMENT: MOD_ASSIGNMENT  -> type(MOD_ASSIGNMENT);
// Inside_ARROW: ARROW  -> type(ARROW);
// Inside_DOUBLE_ARROW: DOUBLE_ARROW  -> type(DOUBLE_ARROW);
// Inside_RANGE: RANGE  -> type(RANGE);
// Inside_RESERVED: RESERVED -> type(RESERVED);
// Inside_COLONCOLON: COLONCOLON  -> type(COLONCOLON);
// Inside_DOUBLE_SEMICOLON: DOUBLE_SEMICOLON  -> type(DOUBLE_SEMICOLON);
// Inside_HASH: HASH  -> type(HASH);
// Inside_AT_NO_WS: AT_NO_WS  -> type(AT_NO_WS);
// Inside_AT_POST_WS: AT_POST_WS  -> type(AT_POST_WS);
// Inside_AT_PRE_WS: AT_PRE_WS  -> type(AT_PRE_WS);
// Inside_AT_BOTH_WS: AT_BOTH_WS  -> type(AT_BOTH_WS);
// Inside_QUEST_WS: '?' (Hidden | NL) -> type(QUEST_WS);
// Inside_QUEST_NO_WS: QUEST_NO_WS -> type(QUEST_NO_WS);
// Inside_LANGLE: LANGLE  -> type(LANGLE);
// Inside_RANGLE: RANGLE  -> type(RANGLE);
// Inside_LE: LE  -> type(LE);
// Inside_GE: GE  -> type(GE);
// Inside_EXCL_EQ: EXCL_EQ  -> type(EXCL_EQ);
// Inside_EXCL_EQEQ: EXCL_EQEQ  -> type(EXCL_EQEQ);
// Inside_IS: IS -> type(IS);
// Inside_NOT_IS: NOT_IS -> type(NOT_IS);
// Inside_NOT_IN: NOT_IN -> type(NOT_IN);
// Inside_AS: AS  -> type(AS);
// Inside_AS_SAFE: AS_SAFE  -> type(AS_SAFE);
// Inside_EQEQ: EQEQ  -> type(EQEQ);
// Inside_EQEQEQ: EQEQEQ  -> type(EQEQEQ);
// Inside_SINGLE_QUOTE: SINGLE_QUOTE  -> type(SINGLE_QUOTE);
// Inside_QUOTE_OPEN: QUOTE_OPEN -> pushMode(LineString), type(QUOTE_OPEN);
// Inside_TRIPLE_QUOTE_OPEN: TRIPLE_QUOTE_OPEN -> pushMode(MultiLineString), type(TRIPLE_QUOTE_OPEN);

// Inside_VAL: VAL -> type(VAL);
// Inside_VAR: VAR -> type(VAR);
// Inside_FUN: FUN -> type(FUN);
// Inside_OBJECT: OBJECT -> type(OBJECT);
// Inside_SUPER: SUPER -> type(SUPER);
// Inside_IN: IN -> type(IN);
// Inside_OUT: OUT -> type(OUT);
// Inside_FIELD: FIELD -> type(FIELD);
// Inside_FILE: FILE -> type(FILE);
// Inside_PROPERTY: PROPERTY -> type(PROPERTY);
// Inside_GET: GET -> type(GET);
// Inside_SET: SET -> type(SET);
// Inside_RECEIVER: RECEIVER -> type(RECEIVER);
// Inside_PARAM: PARAM -> type(PARAM);
// Inside_SETPARAM: SETPARAM -> type(SETPARAM);
// Inside_DELEGATE: DELEGATE -> type(DELEGATE);
// Inside_THROW: THROW -> type(THROW);
// Inside_RETURN: RETURN -> type(RETURN);
// Inside_CONTINUE: CONTINUE -> type(CONTINUE);
// Inside_BREAK: BREAK -> type(BREAK);
// Inside_RETURN_AT: RETURN_AT -> type(RETURN_AT);
// Inside_CONTINUE_AT: CONTINUE_AT -> type(CONTINUE_AT);
// Inside_BREAK_AT: BREAK_AT -> type(BREAK_AT);
// Inside_IF: IF -> type(IF);
// Inside_ELSE: ELSE -> type(ELSE);
// Inside_WHEN: WHEN -> type(WHEN);
// Inside_TRY: TRY -> type(TRY);
// Inside_CATCH: CATCH -> type(CATCH);
// Inside_FINALLY: FINALLY -> type(FINALLY);
// Inside_FOR: FOR -> type(FOR);
// Inside_DO: DO -> type(DO);
// Inside_WHILE: WHILE -> type(WHILE);

// Inside_PUBLIC: PUBLIC -> type(PUBLIC);
// Inside_PRIVATE: PRIVATE -> type(PRIVATE);
// Inside_PROTECTED: PROTECTED -> type(PROTECTED);
// Inside_INTERNAL: INTERNAL -> type(INTERNAL);
// Inside_ENUM: ENUM -> type(ENUM);
// Inside_SEALED: SEALED -> type(SEALED);
// Inside_ANNOTATION: ANNOTATION -> type(ANNOTATION);
// Inside_DATA: DATA -> type(DATA);
// Inside_INNER: INNER -> type(INNER);
// Inside_TAILREC: TAILREC -> type(TAILREC);
// Inside_OPERATOR: OPERATOR -> type(OPERATOR);
// Inside_INLINE: INLINE -> type(INLINE);
// Inside_INFIX: INFIX -> type(INFIX);
// Inside_EXTERNAL: EXTERNAL -> type(EXTERNAL);
// Inside_SUSPEND: SUSPEND -> type(SUSPEND);
// Inside_OVERRIDE: OVERRIDE -> type(OVERRIDE);
// Inside_ABSTRACT: ABSTRACT -> type(ABSTRACT);
// Inside_FINAL: FINAL -> type(FINAL);
// Inside_OPEN: OPEN -> type(OPEN);
// Inside_CONST: CONST -> type(CONST);
// Inside_LATEINIT: LATEINIT -> type(LATEINIT);
// Inside_VARARG: VARARG -> type(VARARG);
// Inside_NOINLINE: NOINLINE -> type(NOINLINE);
// Inside_CROSSINLINE: CROSSINLINE -> type(CROSSINLINE);
// Inside_REIFIED: REIFIED -> type(REIFIED);
// Inside_EXPECT: EXPECT -> type(EXPECT);
// Inside_ACTUAL: ACTUAL -> type(ACTUAL);

// Inside_BooleanLiteral: BooleanLiteral -> type(BooleanLiteral);
// Inside_IntegerLiteral: IntegerLiteral -> type(IntegerLiteral);
// Inside_HexLiteral: HexLiteral -> type(HexLiteral);
// Inside_BinLiteral: BinLiteral -> type(BinLiteral);
// Inside_CharacterLiteral: CharacterLiteral -> type(CharacterLiteral);
// Inside_RealLiteral: RealLiteral -> type(RealLiteral);
// Inside_NullLiteral: NullLiteral -> type(NullLiteral);
// Inside_LongLiteral: LongLiteral -> type(LongLiteral);
// Inside_UnsignedLiteral: UnsignedLiteral -> type(UnsignedLiteral);

// Inside_Identifier: Identifier -> type(Identifier);
// Inside_Comment: (LineComment | DelimitedComment) -> channel(COMMENT);
// Inside_WS: WS -> channel(HIDDEN);
// Inside_NL: NL -> channel(HIDDEN);

// mode DEFAULT_MODE;

// ErrorCharacter: .;

    .convert(
        filter = byDescription("EOF")
    ) { _: AstTerminal ->
        astDrop()
    }
