package com.example.rabbitcalculator.calculator.domain.evaluator

import com.example.rabbitcalculator.calculator.domain.model.Token
import com.example.rabbitcalculator.calculator.domain.model.TokenType

/**
 * 词法分析器
 * 将输入字符串转换为 Token 序列
 */
class Lexer(private val input: String) {
    private var position = 0

    /**
     * 对输入进行词法分析，返回 Token 列表
     */
    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (position < input.length) {
            skipWhitespace()
            if (position >= input.length) break

            val token = nextToken()
            if (token.type == TokenType.ERROR) {
                throw LexerException("Invalid character at position $position: ${token.value}")
            }
            tokens.add(token)
        }
        tokens.add(Token(TokenType.EOF, "", position))
        return tokens
    }

    private fun skipWhitespace() {
        while (position < input.length && input[position].isWhitespace()) {
            position++
        }
    }

    private fun nextToken(): Token {
        val startPos = position
        val char = input[position]

        return when {
            char.isDigit() || char == '.' -> readNumber()
            char.isLetter() -> readIdentifier()
            else -> readOperator(startPos)
        }
    }

    private fun readNumber(): Token {
        val startPos = position
        val builder = StringBuilder()
        var hasDecimal = false

        while (position < input.length) {
            val char = input[position]
            when {
                char.isDigit() -> {
                    builder.append(char)
                    position++
                }
                char == '.' && !hasDecimal -> {
                    builder.append(char)
                    hasDecimal = true
                    position++
                }
                else -> break
            }
        }

        // 处理以小数点开头的情况，如 ".5"
        val value = if (builder.startsWith('.')) {
            "0${builder}"
        } else {
            builder.toString()
        }

        return Token(TokenType.NUMBER, value, startPos)
    }

    private fun readIdentifier(): Token {
        val startPos = position
        val builder = StringBuilder()

        while (position < input.length && (input[position].isLetterOrDigit() || input[position] == '_')) {
            builder.append(input[position])
            position++
        }

        val identifier = builder.toString()
        val type = when (identifier.lowercase()) {
            "sin" -> TokenType.SIN
            "cos" -> TokenType.COS
            "tan" -> TokenType.TAN
            "log" -> TokenType.LOG
            "ln" -> TokenType.LN
            "sqrt" -> TokenType.SQRT
            "abs" -> TokenType.ABS
            "pi", "π" -> TokenType.PI
            "e" -> TokenType.E
            else -> TokenType.ERROR
        }

        return Token(type, identifier, startPos)
    }

    private fun readOperator(startPos: Int): Token {
        val char = input[position]
        position++

        val type = when (char) {
            '+' -> TokenType.PLUS
            '-' -> TokenType.MINUS
            '*' -> TokenType.MULTIPLY
            '/' -> TokenType.DIVIDE
            '^' -> TokenType.POWER
            '!' -> TokenType.FACTORIAL
            '%' -> TokenType.PERCENT
            '(' -> TokenType.LEFT_PAREN
            ')' -> TokenType.RIGHT_PAREN
            'π' -> TokenType.PI
            else -> TokenType.ERROR
        }

        return Token(type, char.toString(), startPos)
    }

    fun reset() {
        position = 0
    }
}

/**
 * 词法分析异常
 */
class LexerException(message: String) : Exception(message)
