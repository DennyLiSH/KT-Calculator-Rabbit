package com.example.rabbitcalculator.calculator.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rabbitcalculator.calculator.data.model.CalculationRecord
import com.example.rabbitcalculator.calculator.data.repository.HistoryRepository
import com.example.rabbitcalculator.calculator.domain.evaluator.AngleUnit
import com.example.rabbitcalculator.calculator.domain.evaluator.EvaluationException
import com.example.rabbitcalculator.calculator.domain.evaluator.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 计算器 ViewModel
 */
class CalculatorViewModel(
    private val evaluator: ExpressionEvaluator,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CalculatorState())
    val state: StateFlow<CalculatorState> = _state.asStateFlow()

    init {
        // 加载历史记录
        loadHistory()
    }

    /**
     * 处理用户操作
     */
    fun processAction(action: UserAction) {
        when (action) {
            // 数字输入
            is UserAction.Digit -> appendDigit(action.digit)
            is UserAction.Decimal -> appendDecimal()

            // 基础运算符
            is UserAction.Plus -> appendOperator("+")
            is UserAction.Minus -> appendOperator("-")
            is UserAction.Multiply -> appendOperator("*")
            is UserAction.Divide -> appendOperator("/")

            // 科学运算符
            is UserAction.Power -> appendOperator("^")
            is UserAction.Factorial -> appendPostfixOperator("!")
            is UserAction.Percent -> appendPostfixOperator("%")

            // 函数
            is UserAction.Function -> appendFunction(action.name)

            // 常量
            is UserAction.Pi -> appendConstant("π")
            is UserAction.E -> appendConstant("e")

            // 括号
            is UserAction.LeftParen -> appendOperator("(")
            is UserAction.RightParen -> appendOperator(")")

            // 控制
            is UserAction.Equals -> calculate()
            is UserAction.Clear -> clear()
            is UserAction.ClearEntry -> clearEntry()
            is UserAction.Backspace -> backspace()

            // 历史记录
            is UserAction.ToggleHistory -> toggleHistory()
            is UserAction.UseHistory -> useHistory(action.record)
            is UserAction.ClearHistory -> clearHistoryAction()

            // 模式切换
            is UserAction.ToggleAngleUnit -> toggleAngleUnit()
        }
    }

    private fun appendDigit(digit: String) {
        _state.update { currentState ->
            val newExpression = if (currentState.isNewInput) {
                digit
            } else {
                currentState.expression + digit
            }
            currentState.copy(
                expression = newExpression,
                isNewInput = false,
                lastActionWasEquals = false
            )
        }
        updateResultPreview()
    }

    private fun appendDecimal() {
        _state.update { currentState ->
            // 检查当前数字是否已经有小数点
            val parts = currentState.expression.split(Regex("[+\\-*/^()]"))
            val lastPart = parts.lastOrNull() ?: ""

            if (lastPart.contains(".")) {
                return@update currentState
            }

            val newExpression = if (currentState.isNewInput) {
                "0."
            } else if (currentState.expression.isEmpty() || currentState.expression.last() in "+-*/^(^") {
                currentState.expression + "0."
            } else {
                currentState.expression + "."
            }
            currentState.copy(
                expression = newExpression,
                isNewInput = false,
                lastActionWasEquals = false
            )
        }
        updateResultPreview()
    }

    private fun appendOperator(operator: String) {
        _state.update { currentState ->
            val newExpression = if (currentState.lastActionWasEquals && currentState.result.isNotEmpty()) {
                // 如果刚按过等号，用结果作为新表达式的开始
                currentState.result + operator
            } else if (currentState.expression.isEmpty()) {
                // 空表达式时，只有减号可以作为负号
                if (operator == "-") operator else return
            } else {
                val lastChar = currentState.expression.last()
                // 如果最后是运算符，替换它（括号除外）
                if (lastChar in "+-*/^" && operator != "(" && operator != ")") {
                    currentState.expression.dropLast(1) + operator
                } else {
                    currentState.expression + operator
                }
            }
            currentState.copy(
                expression = newExpression,
                isNewInput = false,
                lastActionWasEquals = false
            )
        }
        updateResultPreview()
    }

    private fun appendPostfixOperator(operator: String) {
        _state.update { currentState ->
            if (currentState.expression.isEmpty()) return@update currentState

            val newExpression = currentState.expression + operator
            currentState.copy(
                expression = newExpression,
                lastActionWasEquals = false
            )
        }
        updateResultPreview()
    }

    private fun appendFunction(name: String) {
        _state.update { currentState ->
            val functionExpr = "$name("
            val newExpression = if (currentState.lastActionWasEquals && currentState.result.isNotEmpty()) {
                functionExpr
            } else {
                currentState.expression + functionExpr
            }
            currentState.copy(
                expression = newExpression,
                isNewInput = false,
                lastActionWasEquals = false
            )
        }
        updateResultPreview()
    }

    private fun appendConstant(constant: String) {
        _state.update { currentState ->
            val newExpression = if (currentState.isNewInput) {
                constant
            } else {
                currentState.expression + constant
            }
            currentState.copy(
                expression = newExpression,
                isNewInput = false,
                lastActionWasEquals = false
            )
        }
        updateResultPreview()
    }

    private fun calculate() {
        val currentState = _state.value
        if (currentState.expression.isEmpty()) return

        try {
            val result = evaluator.evaluate(currentState.expression)
            val formattedResult = formatResult(result)

            // 保存到历史记录
            viewModelScope.launch {
                historyRepository.saveCalculation(
                    currentState.expression,
                    formattedResult
                )
            }

            _state.update { it.copy(
                result = formattedResult,
                lastActionWasEquals = true,
                isError = false,
                errorMessage = null
            )}
        } catch (e: EvaluationException) {
            _state.update { it.copy(
                isError = true,
                errorMessage = e.message
            )}
        } catch (e: Exception) {
            _state.update { it.copy(
                isError = true,
                errorMessage = "Invalid expression"
            )}
        }
    }

    private fun updateResultPreview() {
        val currentState = _state.value
        if (currentState.expression.isEmpty()) {
            _state.update { it.copy(result = "", isError = false) }
            return
        }

        try {
            val result = evaluator.evaluate(currentState.expression)
            _state.update { it.copy(
                result = formatResult(result),
                isError = false
            )}
        } catch (e: Exception) {
            // 表达式不完整时不显示错误，只清除预览
            _state.update { it.copy(isError = false) }
        }
    }

    private fun formatResult(value: Double): String {
        return when {
            value.isNaN() -> "Error"
            value.isInfinite() -> "∞"
            value == value.toLong().toDouble() -> value.toLong().toString()
            else -> {
                val formatted = String.format(Locale.US, "%.10G", value)
                formatted.removeSuffix(".0").ifEmpty { "0" }
            }
        }
    }

    private fun clear() {
        _state.update { CalculatorState() }
    }

    private fun clearEntry() {
        _state.update { it.copy(
            expression = "",
            result = "",
            isError = false,
            errorMessage = null,
            isNewInput = true
        )}
    }

    private fun backspace() {
        _state.update { currentState ->
            val newExpression = currentState.expression.dropLast(1)
            currentState.copy(
                expression = newExpression,
                isNewInput = newExpression.isEmpty()
            )
        }
        updateResultPreview()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            historyRepository.getHistory().collect { history ->
                _state.update { it.copy(history = history) }
            }
        }
    }

    private fun toggleHistory() {
        _state.update { it.copy(showHistory = !it.showHistory) }
    }

    private fun useHistory(record: CalculationRecord) {
        _state.update { it.copy(
            expression = record.expression,
            result = record.result,
            showHistory = false,
            isNewInput = false,
            lastActionWasEquals = true
        )}
    }

    private fun clearHistoryAction() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }

    private fun toggleAngleUnit() {
        _state.update { currentState ->
            val newAngleUnit = if (currentState.angleUnit == AngleUnit.DEGREE) {
                AngleUnit.RADIAN
            } else {
                AngleUnit.DEGREE
            }
            currentState.copy(angleUnit = newAngleUnit)
        }
        // 重新计算预览
        updateResultPreview()
    }
}
