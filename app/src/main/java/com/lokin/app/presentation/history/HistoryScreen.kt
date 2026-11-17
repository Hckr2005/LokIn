package com.lokin.app.presentation.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit, viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val fmt = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Lịch sử & xu hướng") },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại") }
            }
        )
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                FilterChip(selected = state.days == 7, onClick = { viewModel.setDays(7) }, label = { Text("7 ngày") })
                FilterChip(selected = state.days == 30, onClick = { viewModel.setDays(30) }, label = { Text("30 ngày") })
            }

            // Cân nặng
            ChartCard(
                title = "Cân nặng",
                summary = run {
                    val w = state.weightPoints
                    if (w.size >= 2) {
                        val d = w.last().second - w.first().second
                        "${if (d >= 0) "+" else "−"}${"%.1f".format(kotlin.math.abs(d))} kg so với đầu kỳ"
                    } else if (w.size == 1) "${"%.1f".format(w.first().second)} kg" else null
                },
                dateRange = rangeText(fmt, state.from, state.to)
            ) {
                if (state.weightPoints.isEmpty()) EmptyChart()
                else LineChart(state.weightPoints, state.from, state.to)
            }

            // Nước uống
            ChartCard(
                title = "Nước uống mỗi ngày",
                summary = if (state.waterDaily.isNotEmpty())
                    "Trung bình ${state.waterDaily.average().toInt()} ml/ngày · đường nét đứt là mục tiêu ${state.waterGoal} ml"
                else null,
                dateRange = rangeText(fmt, state.from, state.to)
            ) {
                if (state.waterDaily.all { it == 0f }) EmptyChart()
                else BarChart(state.waterDaily, state.waterGoal.toFloat())
            }

            // Vận động
            ChartCard(
                title = "Calo vận động mỗi ngày",
                summary = if (state.burnDaily.isNotEmpty())
                    "Trung bình ${state.burnDaily.average().toInt()} kcal/ngày · đường nét đứt là mục tiêu ${state.burnGoal} kcal"
                else null,
                dateRange = rangeText(fmt, state.from, state.to)
            ) {
                if (state.burnDaily.all { it == 0f }) EmptyChart()
                else BarChart(state.burnDaily, state.burnGoal.toFloat())
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun rangeText(fmt: SimpleDateFormat, from: Long, to: Long): String =
    "${fmt.format(Date(from))} – ${fmt.format(Date(to - 1))}"

@Composable
private fun ChartCard(title: String, summary: String?, dateRange: String, chart: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (summary != null) {
                Text(summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            chart()
            Text(dateRange, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun EmptyChart() {
    Box(Modifier.fillMaxWidth().height(160.dp)) {
        Text(
            "Chưa có dữ liệu trong khoảng này",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 60.dp)
        )
    }
}