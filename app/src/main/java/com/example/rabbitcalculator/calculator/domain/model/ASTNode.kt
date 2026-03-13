package com.example.rabbitcalculator.calculator.domain.model

import kotlin.math.abs
import kotlin.math.pow

/**
 * 抽象语法树节点
 */
sealed class ASTNode {
    /**
     * 数字节点
     */
    data class NumberNode(val value: Double) : ASTNode()

    /**
     * 二元运算节点
     */
    data class BinaryOpNode(
        val left: ASTNode,
        val operator: TokenType,
        val right: ASTNode
    ) : ASTNode()

    /**
     * 一元运算节点
     * @param isPrefix true 表示前缀运算符（如 -x），false 表示后缀运算符（如 x!）
     */
    data class UnaryOpNode(
        val operator: TokenType,
        val operand: ASTNode,
        val isPrefix: Boolean = true
    ) : ASTNode()

    /**
     * 函数调用节点
     */
    data class FunctionNode(
        val functionName: TokenType,
        val argument: ASTNode
    ) : ASTNode()

    /**
     * 常量节点
     */
    data class ConstantNode(val constantType: TokenType) : ASTNode()
}
