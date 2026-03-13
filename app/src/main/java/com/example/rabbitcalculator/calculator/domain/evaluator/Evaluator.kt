package com.example.rabbitcalculator.calculator.domain.evaluator

import com.example.rabbitcalculator.calculator.domain.model.ASTNode
import com.example.rabbitcalculator.calculator.domain.model.TokenType
import kotlin.math.*

/**
 * 求值器
 * 对 AST 进行求值，返回计算结果
 */
class Evaluator(private val angleUnit: AngleUnit = AngleUnit.DEGREE) {

    /**
     * 对 AST 节点求值
     */
    fun evaluate(node: ASTNode): Double {
        return when (node) {
            is ASTNode.NumberNode -> node.value

            is ASTNode.ConstantNode -> evaluateConstant(node)

            is ASTNode.BinaryOpNode -> evaluateBinaryOp(node)

            is ASTNode.UnaryOpNode -> evaluateUnaryOp(node)

            is ASTNode.FunctionNode -> evaluateFunction(node)
        }
    }

    private fun evaluateConstant(node: ASTNode.ConstantNode): Double {
        return when (node.constantType) {
            TokenType.PI -> PI
            TokenType.E -> E
            else -> throw EvaluationException("Unknown constant: ${node.constantType}")
        }
    }

    private fun evaluateBinaryOp(node: ASTNode.BinaryOpNode): Double {
        val left = evaluate(node.left)
        val right = evaluate(node.right)

        return when (node.operator) {
            TokenType.PLUS -> left + right
            TokenType.MINUS -> left - right
            TokenType.MULTIPLY -> left * right
            TokenType.DIVIDE -> {
                if (right == 0.0) {
                    throw EvaluationException("Division by zero")
                }
                left / right
            }
            TokenType.POWER -> left.pow(right)
            else -> throw EvaluationException("Unknown operator: ${node.operator}")
        }
    }

    private fun evaluateUnaryOp(node: ASTNode.UnaryOpNode): Double {
        val operand = evaluate(node.operand)

        return when (node.operator) {
            TokenType.MINUS -> -operand
            TokenType.FACTORIAL -> {
                if (operand < 0) {
                    throw EvaluationException("Factorial of negative number")
                }
                factorial(operand.toLong())
            }
            TokenType.PERCENT -> operand / 100.0
            else -> throw EvaluationException("Unknown unary operator: ${node.operator}")
        }
    }

    private fun evaluateFunction(node: ASTNode.FunctionNode): Double {
        val arg = evaluate(node.argument)

        return when (node.functionName) {
            TokenType.SIN -> {
                val radians = if (angleUnit == AngleUnit.DEGREE) Math.toRadians(arg) else arg
                sin(radians)
            }
            TokenType.COS -> {
                val radians = if (angleUnit == AngleUnit.DEGREE) Math.toRadians(arg) else arg
                cos(radians)
            }
            TokenType.TAN -> {
                val radians = if (angleUnit == AngleUnit.DEGREE) Math.toRadians(arg) else arg
                tan(radians)
            }
            TokenType.LOG -> log10(arg)
            TokenType.LN -> ln(arg)
            TokenType.SQRT -> {
                if (arg < 0) {
                    throw EvaluationException("Square root of negative number")
                }
                sqrt(arg)
            }
            TokenType.ABS -> abs(arg)
            else -> throw EvaluationException("Unknown function: ${node.functionName}")
        }
    }

    private fun factorial(n: Long): Double {
        if (n > 170) {
            throw EvaluationException("Factorial overflow")
        }
        var result = 1.0
        for (i in 2..n) {
            result *= i
        }
        return result
    }
}

/**
 * 角度单位
 */
enum class AngleUnit {
    DEGREE,
    RADIAN
}

/**
 * 求值异常
 */
class EvaluationException(message: String) : Exception(message)
