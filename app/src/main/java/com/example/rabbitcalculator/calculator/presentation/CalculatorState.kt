package com.example.rabbitcalculator.calculator.presentation

import com.example.rabbitcalculator.calculator.data.model.CalculationRecord
import com.example.rabbitcalculator.calculator.domain.evaluator.AngleUnit

/**
 * 计算器 UI 状态
 */
data class CalculatorState(
    // 表达式显示
    val expression: String = "",
    val displayExpression: String = "",

    // 当前结果（实时预览）
    val result: String = "",
    val isError: Boolean = false,
    val errorMessage: String? = null,

    // 输入状态
    val isNewInput: Boolean = true,
    val lastActionWasEquals: Boolean = false,

    // 历史记录
    val history: List<CalculationRecord> = emptyList(),
    val showHistory: Boolean = false,

    // 模式
    val angleUnit: AngleUnit = AngleUnit.DEGREE
)
