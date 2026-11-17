package com.lokin.app.presentation.ui.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lokin.app.data.local.entity.ExerciseLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(viewModel: ExerciseViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Vận động") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        "Calo tiêu thụ hôm nay", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "${state.todayCalories.toInt()} / ${state.goalCalories} kcal",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                }
            }

            Text(
                "Lịch sử", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (state.logs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Chưa có dữ liệu. Nhấn + để thêm.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.logs, key = { it.id }) { log ->
                        ExerciseLogItem(log) { viewModel.deleteLog(log) }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddExerciseDialog(
            weightKg = state.weightKg,
            onDismiss = { showDialog = false },
            onConfirm = { preset, minutes ->
                viewModel.addLog(preset, minutes)
                showDialog = false
            }
        )
    }
}

@Composable
private fun ExerciseLogItem(log: ExerciseLogEntity, onDelete: () -> Unit) {
    val format = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(log.exerciseType, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${log.durationMin} phút · ${log.caloriesBurned.toInt()} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    format.format(Date(log.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete, contentDescription = "Xóa",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddExerciseDialog(
    weightKg: Float?,
    onDismiss: () -> Unit,
    onConfirm: (ExercisePreset, Int) -> Unit
) {
    var selected by remember { mutableStateOf(exercisePresets.first()) }
    var duration by remember { mutableStateOf("30") }
    val minutes = duration.toIntOrNull()?.takeIf { it in 1..600 }
    val kcal = if (minutes != null && weightKg != null) selected.met * weightKg * minutes / 60f else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ghi nhận vận động") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    exercisePresets.forEach { p ->
                        FilterChip(
                            selected = p == selected,
                            onClick = { selected = p },
                            label = { Text(p.name) }
                        )
                    }
                }
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it.filter(Char::isDigit) },
                    label = { Text("Thời gian (phút)") },
                    isError = duration.isNotBlank() && minutes == null,
                    supportingText = {
                        if (duration.isNotBlank() && minutes == null) Text("Nhập từ 1 đến 600 phút")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                when {
                    weightKg == null -> Text(
                        "Hãy nhập cân nặng ở tab Cá nhân để tính calo.",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    kcal != null -> Text("≈ ${kcal.toInt()} kcal", fontWeight = FontWeight.SemiBold)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { minutes?.let { onConfirm(selected, it) } },
                enabled = minutes != null && weightKg != null
            ) { Text("Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}