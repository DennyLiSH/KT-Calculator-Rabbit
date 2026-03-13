package com.example.rabbitcalculator

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.rabbitcalculator.calculator.presentation.CalculatorScreen
import com.example.rabbitcalculator.calculator.presentation.CalculatorState
import com.example.rabbitcalculator.calculator.presentation.UserAction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 计算器布局测试
 * 验证所有按钮的可见性和功能
 */
@RunWith(AndroidJUnit4::class)
class CalculatorLayoutTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ==================== 按钮可见性测试 ====================

    @Test
    fun allNumberButtons_areVisible() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = {}
            )
        }

        // 验证所有数字按钮 (0-9) 都存在且可点击
        val numberButtons = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        numberButtons.forEach { digit ->
            composeTestRule
                .onNodeWithText(digit, useUnmergedTree = true)
                .assertExists("数字按钮 $digit 应该存在")
                .assertIsEnabled()
        }
    }

    @Test
    fun allOperatorButtons_areVisible() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = {}
            )
        }

        // 验证所有运算符按钮都存在且可点击
        val operators = listOf("÷", "×", "−", "+") // Unicode 字符
        operators.forEach { op ->
            composeTestRule
                .onNodeWithText(op, useUnmergedTree = true)
                .assertExists("运算符按钮 $op 应该存在")
                .assertIsEnabled()
        }
    }

    @Test
    fun equalsButton_isVisible() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = {}
            )
        }

        composeTestRule
            .onNodeWithText("=", useUnmergedTree = true)
            .assertExists("等号按钮应该存在")
            .assertIsEnabled()
    }

    @Test
    fun clearButton_isVisible() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = {}
            )
        }

        composeTestRule
            .onNodeWithText("C", useUnmergedTree = true)
            .assertExists("清除按钮应该存在")
            .assertIsEnabled()
    }

    // ==================== 按钮功能测试 ====================

    @Test
    fun numberButton_click_updatesExpression() {
        var clickedAction: UserAction? = null

        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = { clickedAction = it }
            )
        }

        composeTestRule
            .onNodeWithText("5", useUnmergedTree = true)
            .performClick()

        assert(clickedAction is UserAction.Digit) {
            "点击数字按钮应该触发 Digit action"
        }
        assert((clickedAction as UserAction.Digit).value == "5") {
            "点击 5 应该触发 Digit(\"5\")"
        }
    }

    @Test
    fun operatorButton_click_updatesExpression() {
        var clickedAction: UserAction? = null

        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = { clickedAction = it }
            )
        }

        composeTestRule
            .onNodeWithText("+", useUnmergedTree = true)
            .performClick()

        assert(clickedAction is UserAction.Plus) {
            "点击加号按钮应该触发 Plus action"
        }
    }

    @Test
    fun equalsButton_click_triggersCalculate() {
        var clickedAction: UserAction? = null

        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = { clickedAction = it }
            )
        }

        composeTestRule
            .onNodeWithText("=", useUnmergedTree = true)
            .performClick()

        assert(clickedAction is UserAction.Equals) {
            "点击等号按钮应该触发 Equals action"
        }
    }

    // ==================== 科学函数按钮测试 ====================

    @Test
    fun scientificFunctionButtons_areVisible() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = {}
            )
        }

        val functions = listOf("sin", "cos", "tan", "log", "ln", "√", "π", "e", "^", "!")
        functions.forEach { func ->
            composeTestRule
                .onNodeWithText(func, useUnmergedTree = true)
                .assertExists("科学函数按钮 $func 应该存在")
        }
    }

    @Test
    fun parenthesisButtons_areVisible() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(),
                onAction = {}
            )
        }

        composeTestRule
            .onNodeWithText("(", useUnmergedTree = true)
            .assertExists("左括号按钮应该存在")

        composeTestRule
            .onNodeWithText(")", useUnmergedTree = true)
            .assertExists("右括号按钮应该存在")
    }

    // ==================== 显示区域测试 ====================

    @Test
    fun display_showsExpression() {
        val testExpression = "2+3"

        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(expression = testExpression),
                onAction = {}
            )
        }

        composeTestRule
            .onNodeWithText(testExpression, useUnmergedTree = true)
            .assertExists("显示区域应该显示表达式")
    }

    @Test
    fun display_showsResult() {
        val testResult = "5"

        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(result = testResult),
                onAction = {}
            )
        }

        composeTestRule
            .onNodeWithText(testResult, useUnmergedTree = true)
            .assertExists("显示区域应该显示结果")
    }

    @Test
    fun display_showsError() {
        composeTestRule.setContent {
            CalculatorScreen(
                state = CalculatorState(
                    isError = true,
                    errorMessage = "Error: Division by zero"
                ),
                onAction = {}
            )
        }

        composeTestRule
            .onNodeWithText("Error: Division by zero", useUnmergedTree = true)
            .assertExists("显示区域应该显示错误信息")
    }
}
