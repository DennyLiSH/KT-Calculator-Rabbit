package com.example.rabbitcalculator.calculator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room 数据库
 */
@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
