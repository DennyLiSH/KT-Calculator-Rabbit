package com.example.rabbitcalculator.calculator.domain.model

/**
 * 词法单元，由 Lexer 产生
 */
data class Token(
    val type: TokenType,
    val value: String,
    val position: Int = 0
) {
    override fun toString(): String = when (type) {
        TokenType.NUMBER -> "NUMBER($value)"
        TokenType.EOF -> "EOF"
        TokenType.ERROR -> "ERROR($value)"
        else -> type.name
    }
}
