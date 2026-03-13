package com.example.rabbitcalculator.calculator.data.repository

import com.example.rabbitcalculator.calculator.data.local.HistoryDao
import com.example.rabbitcalculator.calculator.data.local.HistoryEntity
import com.example.rabbitcalculator.calculator.data.model.CalculationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 历史记录仓库接口
 */
interface HistoryRepository {
    fun getHistory(): Flow<List<CalculationRecord>>
    suspend fun saveCalculation(expression: String, result: String)
    suspend fun clearHistory()
}

/**
 * 历史记录仓库实现
 */
class HistoryRepositoryImpl(
    private val dao: HistoryDao
) : HistoryRepository {

    override fun getHistory(): Flow<List<CalculationRecord>> =
        dao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveCalculation(expression: String, result: String) {
        dao.insert(HistoryEntity(
            expression = expression,
            result = result
        ))
    }

    override suspend fun clearHistory() {
        dao.deleteAll()
    }
}

// 扩展函数在 HistoryEntity.kt 中定义
private fun HistoryEntity.toDomain(): CalculationRecord = CalculationRecord(
    id = id,
    expression = expression,
    result = result,
    timestamp = timestamp
)
