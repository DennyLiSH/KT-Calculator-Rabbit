package com.example.rabbitcalculator.calculator.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * 历史记录数据访问对象
 */
@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Insert
    suspend fun insert(entity: HistoryEntity): Long

    @Query("DELETE FROM calculation_history")
    suspend fun deleteAll()

    @Query("DELETE FROM calculation_history WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long)
}
