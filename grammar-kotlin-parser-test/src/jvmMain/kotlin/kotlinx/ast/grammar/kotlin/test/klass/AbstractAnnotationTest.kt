package kotlinx.ast.grammar.kotlin.test.klass

import kotlinx.ast.common.klass.*
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParser
import kotlinx.ast.grammar.kotlin.common.KotlinGrammarParserType

abstract class AbstractAnnotationTest<Parser : KotlinGrammarParser<*, *>>(parser: Parser) :
    AbstractKlassTest<Parser>(
        parser,
        "@some.where.Class<Hello>.Annotation5(\"hello\", \"world\")",
        TestCase(
            name = "AbstractAnnotationTest",
            parserTypes = listOf(
                KotlinGrammarParserType.`annotation`
            ),
            print = """
                annotation
                  singleAnnotation
                    AT_NO_WS >>>@<<< (DEFAULT_TOKEN_CHANNEL)
                    unescapedAnnotation
                      constructorInvocation
                        userType
                          simpleUserType
                            simpleIdentifier
                              Identifier >>>some<<< (DEFAULT_TOKEN_CHANNEL)
                          DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                          simpleUserType
                            simpleIdentifier
                              WHERE >>>where<<< (DEFAULT_TOKEN_CHANNEL)
                          DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                          simpleUserType
                            simpleIdentifier
                              Identifier >>>Class<<< (DEFAULT_TOKEN_CHANNEL)
                            typeArguments
                              LANGLE >>><<<< (DEFAULT_TOKEN_CHANNEL)
                              typeProjection
                                type
                                  typeReference
                                    userType
                                      simpleUserType
                                        simpleIdentifier
                                          Identifier >>>Hello<<< (DEFAULT_TOKEN_CHANNEL)
                              RANGLE >>>><<< (DEFAULT_TOKEN_CHANNEL)
                          DOT >>>.<<< (DEFAULT_TOKEN_CHANNEL)
                          simpleUserType
                            simpleIdentifier
                              Identifier >>>Annotation5<<< (DEFAULT_TOKEN_CHANNEL)
                        valueArguments
                          LPAREN >>>(<<< (DEFAULT_TOKEN_CHANNEL)
                          valueArgument
                            expression
                              disjunction
                                conjunction
                                  equality
                                    comparison
                                      infixOperation
                                        elvisExpression
                                          infixFunctionCall
                                            rangeExpression
                                              additiveExpression
                                                multiplicativeExpression
                                                  asExpression
                                                    prefixUnaryExpression
                                                      postfixUnaryExpression
                                                        primaryExpression
                                                          stringLiteral
                                                            lineStringLiteral
                                                              QUOTE_OPEN >>>"<<< (DEFAULT_TOKEN_CHANNEL)
                                                              lineStringContent
                                                                LineStrText >>>hello<<< (DEFAULT_TOKEN_CHANNEL)
                                                              QUOTE_CLOSE >>>"<<< (DEFAULT_TOKEN_CHANNEL)
                          COMMA >>>,<<< (DEFAULT_TOKEN_CHANNEL)
                          Inside_WS >>> <<< (HIDDEN)
                          valueArgument
                            expression
                              disjunction
                                conjunction
                                  equality
                                    comparison
                                      infixOperation
                                        elvisExpression
                                          infixFunctionCall
                                            rangeExpression
                                              additiveExpression
                                                multiplicativeExpression
                                                  asExpression
                                                    prefixUnaryExpression
                                                      postfixUnaryExpression
                                                        primaryExpression
                                                          stringLiteral
                                                            lineStringLiteral
                                                              QUOTE_OPEN >>>"<<< (DEFAULT_TOKEN_CHANNEL)
                                                              lineStringContent
                                                                LineStrText >>>world<<< (DEFAULT_TOKEN_CHANNEL)
                                                              QUOTE_CLOSE >>>"<<< (DEFAULT_TOKEN_CHANNEL)
                          RPAREN >>>)<<< (DEFAULT_TOKEN_CHANNEL)
                """.trimIndent(),
            summary = listOf(
                KlassAnnotation(
                    identifier = listOf(
                        KlassIdentifier("some", raw = null),
                        KlassIdentifier("where", raw = null),
                        KlassIdentifier("Class", raw = null)
                            .parameterizedBy(KlassIdentifier("Hello", raw = null)),
                        KlassIdentifier("Annotation5", raw = null)
                    ),
                    arguments = listOf(
                        KlassDeclaration(
                            keyword = "argument",
                            expressions = listOf(
                                KlassString(
                                    "hello".asStringComponentRaw(),
                                    raw = null
                                )
                            ),
                            raw = null
                        ),
                        KlassDeclaration(
                            keyword = "argument",
                            expressions = listOf(
                                KlassString(
                                    "world".asStringComponentRaw(),
                                    raw = null
                                )
                            ),
                            raw = null
                        )
                    ),
                    raw = null
                )
            )
        )
    )
