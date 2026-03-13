package com.example.rabbitcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rabbitcalculator.calculator.presentation.CalculatorScreen
import com.example.rabbitcalculator.calculator.presentation.CalculatorViewModel
import com.example.rabbitcalculator.calculator.presentation.UserAction
import com.example.rabbitcalculator.di.AppContainer
import com.example.rabbitcalculator.ui.theme.RabbitCalculatorTheme

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(applicationContext)
        enableEdgeToEdge()

        setContent {
            RabbitCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: CalculatorViewModel = viewModel(
                        factory = CalculatorViewModelFactory(appContainer)
                    )
                    val state by viewModel.state.collectAsState()

                    CalculatorScreen(
                        state = state,
                        onAction = { action: UserAction ->
                            viewModel.processAction(action)
                        }
                    )
                }
            }
        }
    }
}
