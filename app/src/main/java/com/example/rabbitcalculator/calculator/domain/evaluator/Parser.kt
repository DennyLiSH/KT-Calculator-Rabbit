package com.example.rabbitcalculator.calculator.domain.evaluator

import com.example.rabbitcalculator.calculator.domain.model.ASTNode
import com.example.rabbitcalculator.calculator.domain.model.Token
import com.example.rabbitcalculator.calculator.domain.model.TokenType

/**
 * 语法分析器（递归下降解析器）
 * 将 Token 序列转换为 AST（抽象语法树）
 *
 * 运算符优先级（从高到低）：
 * 1. 括号 ()
 * 2. 函数调用
 * 3. 后缀运算符 ! %
 * 4. 幂运算 ^ （右结合）
 * 5. 前缀运算符 - （一元负号）
 * 6. 乘除 * /
 * 7. 加减 + -
 */
class Parser(private val tokens: List<Token>) {
    private var current = 0

    /**
     * 解析表达式，返回 AST 根节点
     */
    fun parse(): ASTNode {
        val node = parseExpression()
        if (!isAtEnd()) {
            throw ParserException("Unexpected token: ${peek()}")
        }
        return node
    }

    // expression := term (('+' | '-') term)*
    private fun parseExpression(): ASTNode {
        var left = parseTerm()

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val operator = previous().type
            val right = parseTerm()
            left = ASTNode.BinaryOpNode(left, operator, right)
        }

        return left
    }

    // term := power (('*' | '/') power)*
    private fun parseTerm(): ASTNode {
        var left = parsePower()

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            val operator = previous().type
            val right = parsePower()
            left = ASTNode.BinaryOpNode(left, operator, right)
        }

        return left
    }

    // power := unary ('^' power)?  // 右结合
    private fun parsePower(): ASTNode {
        val left = parseUnary()

        if (match(TokenType.POWER)) {
            val right = parsePower() // 右结合，递归调用自身
            return ASTNode.BinaryOpNode(left, TokenType.POWER, right)
        }

        return left
    }

    // unary := ('-')* postfix
    private fun parseUnary(): ASTNode {
        if (match(TokenType.MINUS)) {
            val operand = parseUnary()
            return ASTNode.UnaryOpNode(TokenType.MINUS, operand, isPrefix = true)
        }
        return parsePostfix()
    }

    // postfix := primary ('!' | '%')*
    private fun parsePostfix(): ASTNode {
        var node = parsePrimary()

        while (match(TokenType.FACTORIAL, TokenType.PERCENT)) {
            val operator = previous().type
            node = ASTNode.UnaryOpNode(operator, node, isPrefix = false)
        }

        return node
    }

    // primary := NUMBER | CONSTANT | FUNCTION '(' expression ')' | '(' expression ')'
    private fun parsePrimary(): ASTNode {
        // 数字
        if (match(TokenType.NUMBER)) {
            val value = previous().value.toDouble()
            return ASTNode.NumberNode(value)
        }

        // 常量
        if (match(TokenType.PI)) {
            return ASTNode.ConstantNode(TokenType.PI)
        }
        if (match(TokenType.E)) {
            return ASTNode.ConstantNode(TokenType.E)
        }

        // 函数
        if (match(TokenType.SIN, TokenType.COS, TokenType.TAN,
                  TokenType.LOG, TokenType.LN, TokenType.SQRT, TokenType.ABS)) {
            val functionToken = previous()
            consume(TokenType.LEFT_PAREN, "Expected '(' after function name")
            val argument = parseExpression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after function argument")
            return ASTNode.FunctionNode(functionToken.type, argument)
        }

        // 括号
        if (match(TokenType.LEFT_PAREN)) {
            val node = parseExpression()
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression")
            return node
        }

        throw ParserException("Unexpected token: ${peek()}")
    }

    // 辅助方法
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        return !isAtEnd() && peek().type == type
    }

    private fun advance(): Token {
        if (!isAtEnd()) current++
        return previous()
    }

    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) return advance()
        throw ParserException(message)
    }

    private fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current - 1]

    private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
}

/**
 * 语法分析异常
 */
class ParserException(message: String) : Exception(message)
