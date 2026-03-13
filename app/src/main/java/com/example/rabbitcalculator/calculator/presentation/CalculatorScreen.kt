package com.example.rabbitcalculator.calculator.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rabbitcalculator.calculator.data.model.CalculationRecord
import com.example.rabbitcalculator.calculator.domain.evaluator.AngleUnit
import com.example.rabbitcalculator.ui.components.ButtonStyle
import com.example.rabbitcalculator.ui.components.CalculatorButton
import com.example.rabbitcalculator.ui.components.CalculatorDisplay
import java.text.SimpleDateFormat
import java.util.*

/**
 * 计算器主屏幕
 */
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (UserAction) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        LandscapeLayout(state, onAction)
    } else {
        PortraitLayout(state, onAction)
    }
}

/**
 * 竖屏布局
 */
@Composable
private fun PortraitLayout(
    state: CalculatorState,
    onAction: (UserAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 显示区域
        CalculatorDisplay(
            expression = state.expression,
            result = state.result,
            isError = state.isError,
            modifier = Modifier.weight(1f)
        )

        // 角度单位指示器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { onAction(UserAction.ToggleAngleUnit) }) {
                Text(
                    text = if (state.angleUnit == AngleUnit.DEGREE) "DEG" else "RAD",
                    style = MaterialTheme.typography.labelMedium
                )
            }
            TextButton(onClick = { onAction(UserAction.ToggleHistory) }) {
                Text("History", style = MaterialTheme.typography.labelMedium)
            }
        }

        // 历史记录面板
        if (state.showHistory) {
            HistoryPanel(
                history = state.history,
                onSelect = { onAction(UserAction.UseHistory(it)) },
                onClear = { onAction(UserAction.ClearHistory) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 科学函数行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ScientificButton("sin", Modifier.weight(1f)) { onAction(UserAction.Function("sin")) }
            ScientificButton("cos", Modifier.weight(1f)) { onAction(UserAction.Function("cos")) }
            ScientificButton("tan", Modifier.weight(1f)) { onAction(UserAction.Function("tan")) }
            CalculatorButton(
                text = "(",
                style = ButtonStyle.FUNCTION,
                onClick = { onAction(UserAction.LeftParen) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ScientificButton("log", Modifier.weight(1f)) { onAction(UserAction.Function("log")) }
            ScientificButton("ln", Modifier.weight(1f)) { onAction(UserAction.Function("ln")) }
            ScientificButton("√", Modifier.weight(1f)) { onAction(UserAction.Function("sqrt")) }
            CalculatorButton(
                text = ")",
                style = ButtonStyle.FUNCTION,
                onClick = { onAction(UserAction.RightParen) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(
                text = "π",
                style = ButtonStyle.FUNCTION,
                onClick = { onAction(UserAction.Pi) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            CalculatorButton(
                text = "e",
                style = ButtonStyle.FUNCTION,
                onClick = { onAction(UserAction.E) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            CalculatorButton(
                text = "^",
                style = ButtonStyle.OPERATOR,
                onClick = { onAction(UserAction.Power) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            CalculatorButton(
                text = "!",
                style = ButtonStyle.OPERATOR,
                onClick = { onAction(UserAction.Factorial) },
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 主键盘
        MainKeyboard(state, onAction)

        // 等号按钮
        Spacer(modifier = Modifier.height(4.dp))
        CalculatorButton(
            text = "=",
            style = ButtonStyle.OPERATOR,
            onClick = { onAction(UserAction.Equals) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )
    }
}

/**
 * 横屏布局
 */
@Composable
private fun LandscapeLayout(
    state: CalculatorState,
    onAction: (UserAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 左侧：显示和键盘
        Column(
            modifier = Modifier.weight(2f)
        ) {
            CalculatorDisplay(
                expression = state.expression,
                result = state.result,
                isError = state.isError,
                modifier = Modifier.weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TextButton(onClick = { onAction(UserAction.ToggleAngleUnit) }) {
                    Text(if (state.angleUnit == AngleUnit.DEGREE) "DEG" else "RAD")
                }
                TextButton(onClick = { onAction(UserAction.ToggleHistory) }) {
                    Text("History")
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            MainKeyboard(state, onAction)
        }

        // 右侧：历史记录
        if (state.showHistory) {
            Spacer(modifier = Modifier.width(8.dp))
            HistoryPanel(
                history = state.history,
                onSelect = { onAction(UserAction.UseHistory(it)) },
                onClear = { onAction(UserAction.ClearHistory) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 主键盘（数字和基础运算符）
 */
@Composable
private fun MainKeyboard(
    state: CalculatorState,
    onAction: (UserAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 7 8 9 /
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton("7", ButtonStyle.NUMBER, { onAction(UserAction.Digit("7")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("8", ButtonStyle.NUMBER, { onAction(UserAction.Digit("8")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("9", ButtonStyle.NUMBER, { onAction(UserAction.Digit("9")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("÷", ButtonStyle.OPERATOR, { onAction(UserAction.Divide) }, Modifier.weight(1f).aspectRatio(1f))
        }

        // 4 5 6 ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton("4", ButtonStyle.NUMBER, { onAction(UserAction.Digit("4")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("5", ButtonStyle.NUMBER, { onAction(UserAction.Digit("5")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("6", ButtonStyle.NUMBER, { onAction(UserAction.Digit("6")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("×", ButtonStyle.OPERATOR, { onAction(UserAction.Multiply) }, Modifier.weight(1f).aspectRatio(1f))
        }

        // 1 2 3 -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton("1", ButtonStyle.NUMBER, { onAction(UserAction.Digit("1")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("2", ButtonStyle.NUMBER, { onAction(UserAction.Digit("2")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("3", ButtonStyle.NUMBER, { onAction(UserAction.Digit("3")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("−", ButtonStyle.OPERATOR, { onAction(UserAction.Minus) }, Modifier.weight(1f).aspectRatio(1f))
        }

        // C ⌫ 0 . +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton("C", ButtonStyle.CONTROL, { onAction(UserAction.Clear) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("⌫", ButtonStyle.CONTROL, { onAction(UserAction.Backspace) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("0", ButtonStyle.NUMBER, { onAction(UserAction.Digit("0")) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton(".", ButtonStyle.NUMBER, { onAction(UserAction.Decimal) }, Modifier.weight(1f).aspectRatio(1f))
            CalculatorButton("+", ButtonStyle.OPERATOR, { onAction(UserAction.Plus) }, Modifier.weight(1f).aspectRatio(1f))
        }
    }
}

/**
 * 科学计算按钮（小号）
 */
@Composable
private fun ScientificButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

/**
 * 历史记录面板
 */
@Composable
private fun HistoryPanel(
    history: List<CalculationRecord>,
    onSelect: (CalculationRecord) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium
                )
                TextButton(onClick = onClear) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            }

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No history",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(history) { record ->
                        HistoryItem(
                            record = record,
                            onClick = { onSelect(record) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 历史记录项
 */
@Composable
private fun HistoryItem(
    record: CalculationRecord,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = record.expression,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "= ${record.result}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = dateFormat.format(Date(record.timestamp)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
