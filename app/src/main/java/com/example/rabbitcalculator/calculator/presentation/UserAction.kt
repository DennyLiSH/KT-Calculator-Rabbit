package com.example.rabbitcalculator.calculator.presentation

import com.example.rabbitcalculator.calculator.data.model.CalculationRecord

/**
 * 用户操作
 */
sealed class UserAction {
    // 数字输入
    data class Digit(val digit: String) : UserAction()
    data object Decimal : UserAction()

    // 基础运算符
    data object Plus : UserAction()
    data object Minus : UserAction()
    data object Multiply : UserAction()
    data object Divide : UserAction()

    // 科学运算符
    data object Power : UserAction()
    data object Factorial : UserAction()
    data object Percent : UserAction()

    // 函数
    data class Function(val name: String) : UserAction()

    // 常量
    data object Pi : UserAction()
    data object E : UserAction()

    // 括号
    data object LeftParen : UserAction()
    data object RightParen : UserAction()

    // 控制
    data object Equals : UserAction()
    data object Clear : UserAction()
    data object ClearEntry : UserAction()
    data object Backspace : UserAction()

    // 历史记录
    data object ToggleHistory : UserAction()
    data class UseHistory(val record: CalculationRecord) : UserAction()
    data object ClearHistory : UserAction()

    // 模式切换
    data object ToggleAngleUnit : UserAction()
}
