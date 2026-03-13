package com.example.rabbitcalculator.calculator.data.model

/**
 * 计算记录（领域模型）
 */
data class CalculationRecord(
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
