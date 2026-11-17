package com.lokin.app.presentation.ui.goals

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private fun String.toNum() = replace(',', '.').toFloatOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(onBack: () -> Unit, viewModel: GoalsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val user = state.user

    var goalW by remember(user) { mutableStateOf(user?.goalWeightKg?.takeIf { it > 0 }?.let { "%.1f".format(it) } ?: "") }
    var water by remember(user) { mutableStateOf((user?.dailyWaterGoalMl ?: 2000).toString()) }
    var calIn by remember(user) { mutableStateOf((user?.dailyCalorieGoal ?: 2000).toString()) }
    var steps by remember(user) { mutableStateOf((user?.dailyStepGoal ?: 10000).toString()) }
    var burn by remember(user) { mutableStateOf((user?.dailyCalorieBurnGoal ?: 500).toString()) }

    val goalV = goalW.toNum()?.takeIf { it in 20f..300f }
    val waterV = water.toIntOrNull()?.takeIf { it in 500..6000 }
    val calV = calIn.toIntOrNull()?.takeIf { it in 800..6000 }
    val stepsV = steps.toIntOrNull()?.takeIf { it in 1000..50000 }
    val burnV = burn.toIntOrNull()?.takeIf { it in 50..3000 }
    val canSave = goalV != null && waterV != null && calV != null && stepsV != null && burnV != null

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Mục tiêu") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại") }
            }
        )
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            val progress = state.weightProgress
            if (user != null && progress != null && state.currentWeight != null) {
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "Tiến độ cân nặng", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            "${"%.1f".format(user.weightKg)} → ${"%.1f".format(state.currentWeight)} → ${"%.1f".format(user.goalWeightKg)} kg",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            "Ban đầu → Hiện tại → Mục tiêu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp))
                        Text(
                            "${(progress * 100).toInt()}% hành trình",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            GoalField("Cân nặng mục tiêu (kg)", goalW, { goalW = it.filter { c -> c.isDigit() || c == '.' || c == ',' } }, goalW.isNotBlank() && goalV == null, "20 – 300 kg", KeyboardType.Decimal)
            GoalField("Nước mỗi ngày (ml)", water, { water = it.filter(Char::isDigit) }, water.isNotBlank() && waterV == null, "500 – 6000 ml")
            GoalField("Calo nạp vào mỗi ngày (kcal)", calIn, { calIn = it.filter(Char::isDigit) }, calIn.isNotBlank() && calV == null, "800 – 6000 kcal")
            GoalField("Bước chân mỗi ngày", steps, { steps = it.filter(Char::isDigit) }, steps.isNotBlank() && stepsV == null, "1000 – 50000 bước")
            GoalField("Calo tiêu thụ khi vận động (kcal)", burn, { burn = it.filter(Char::isDigit) }, burn.isNotBlank() && burnV == null, "50 – 3000 kcal")

            Button(
                onClick = {
                    viewModel.save(goalV!!, waterV!!, calV!!, stepsV!!, burnV!!)
                    Toast.makeText(context, "Đã lưu mục tiêu", Toast.LENGTH_SHORT).show()
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Lưu mục tiêu") }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GoalField(
    label: String, value: String, onChange: (String) -> Unit,
    isError: Boolean, hint: String, keyboard: KeyboardType = KeyboardType.Number
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label) },
        isError = isError,
        supportingText = { if (isError) Text("Nhập trong khoảng $hint") },
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}