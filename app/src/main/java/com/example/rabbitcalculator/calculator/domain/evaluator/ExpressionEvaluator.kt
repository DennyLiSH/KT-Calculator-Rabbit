package com.example.rabbitcalculator.calculator.domain.evaluator

/**
 * 表达式求值器（统一入口）
 * 整合 Lexer -> Parser -> Evaluator 流程
 */
class ExpressionEvaluator(var angleUnit: AngleUnit = AngleUnit.DEGREE) {

    /**
     * 对表达式字符串求值
     * @param expression 数学表达式字符串
     * @return 计算结果
     */
    fun evaluate(expression: String): Double {
        if (expression.isBlank()) {
            throw EvaluationException("Empty expression")
        }

        // 1. 词法分析
        val lexer = Lexer(expression)
        val tokens = lexer.tokenize()

        // 2. 语法分析
        val parser = Parser(tokens)
        val ast = parser.parse()

        // 3. 求值
        val evaluator = Evaluator(angleUnit)
        return evaluator.evaluate(ast)
    }

    /**
     * 验证表达式是否有效
     */
    fun isValid(expression: String): Boolean {
        return try {
            evaluate(expression)
            true
        } catch (e: Exception) {
            false
        }
    }
}
