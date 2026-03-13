package com.example.rabbitcalculator.calculator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rabbitcalculator.calculator.data.model.CalculationRecord

/**
 * 历史记录数据库实体
 */
@Entity(tableName = "calculation_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * 将 Entity 转换为领域模型
 */
fun HistoryEntity.toDomain(): CalculationRecord = CalculationRecord(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)

/**
 * 将领域模型转换为 Entity
 */
fun CalculationRecord.toEntity(): HistoryEntity = HistoryEntity(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)
