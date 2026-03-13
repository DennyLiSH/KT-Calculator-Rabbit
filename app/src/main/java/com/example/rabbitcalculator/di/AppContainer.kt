package com.example.rabbitcalculator.di

import android.content.Context
import androidx.room.Room
import com.example.rabbitcalculator.calculator.data.local.HistoryDatabase
import com.example.rabbitcalculator.calculator.data.repository.HistoryRepository
import com.example.rabbitcalculator.calculator.data.repository.HistoryRepositoryImpl
import com.example.rabbitcalculator.calculator.domain.evaluator.AngleUnit
import com.example.rabbitcalculator.calculator.domain.evaluator.ExpressionEvaluator
import com.example.rabbitcalculator.calculator.presentation.CalculatorViewModel

/**
 * 应用依赖容器
 */
class AppContainer(context: Context) {

    // 数据库
    private val database: HistoryDatabase = Room.databaseBuilder(
        context.applicationContext,
        HistoryDatabase::class.java,
        "calculator_history"
    ).build()

    // DAO
    private val historyDao = database.historyDao()

    // Repository
    private val historyRepository: HistoryRepository = HistoryRepositoryImpl(historyDao)

    // 表达式求值器
    fun createEvaluator(angleUnit: AngleUnit = AngleUnit.DEGREE): ExpressionEvaluator {
        return ExpressionEvaluator(angleUnit)
    }

    // ViewModel 工厂方法
    fun createCalculatorViewModel(): CalculatorViewModel {
        return CalculatorViewModel(
            evaluator = createEvaluator(),
            historyRepository = historyRepository
        )
    }
}
