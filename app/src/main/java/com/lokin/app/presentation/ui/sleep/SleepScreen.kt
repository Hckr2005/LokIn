package com.lokin.app.presentation.ui.sleep

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lokin.app.data.local.entity.SleepLogEntity
import com.lokin.app.util.SleepUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(onBack: () -> Unit, viewModel: SleepViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val last = state.logs.firstOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giấc ngủ") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        "Đêm gần nhất", style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (last != null) {
                        val h = SleepUtil.hours(last)
                        Text(
                            "${"%.1f".format(h)} giờ",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        val verdict = when {
                            h < state.recMin -> "Thiếu so với khuyến nghị"
                            h > state.recMax -> "Nhiều hơn khuyến nghị"
                            else -> "Đạt khuyến nghị"
                        }
                        Text(verdict, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Chưa có dữ liệu", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Khuyến nghị theo độ tuổi: ${state.recMin}–${state.recMax} giờ/đêm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    state.avgHours?.let {
                        Text(
                            "Trung bình 7 đêm gần nhất: ${"%.1f".format(it)} giờ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Text("Lịch sử", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))

            if (state.logs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có dữ liệu. Nhấn + để thêm.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.logs, key = { it.id }) { log -> SleepLogItem(log) { viewModel.deleteLog(log) } }
                }
            }
        }
    }

    if (showDialog) {
        AddSleepDialog(
            onDismiss = { showDialog = false },
            onConfirm = { bed, wake, quality ->
                viewModel.addLog(bed, wake, quality)
                showDialog = false
            }
        )
    }
}

@Composable
private fun SleepLogItem(log: SleepLogEntity, onDelete: () -> Unit) {
    val day = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val clock = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "${"%.1f".format(SleepUtil.hours(log))} giờ · Chất lượng ${log.quality}/5",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${clock.format(Date(log.bedtimeMs))} → ${clock.format(Date(log.wakeTimeMs))}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    day.format(Date(log.wakeTimeMs)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

private fun computeRange(bedH: Int, bedM: Int, wakeH: Int, wakeM: Int): Pair<Long, Long> {
    val wake = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, wakeH); set(Calendar.MINUTE, wakeM)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    if (wake.timeInMillis > System.currentTimeMillis()) wake.add(Calendar.DAY_OF_YEAR, -1)
    val bed = (wake.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, bedH); set(Calendar.MINUTE, bedM)
    }
    if (bed.timeInMillis >= wake.timeInMillis) bed.add(Calendar.DAY_OF_YEAR, -1)
    return bed.timeInMillis to wake.timeInMillis
}

@Composable
private fun AddSleepDialog(onDismiss: () -> Unit, onConfirm: (Long, Long, Int) -> Unit) {
    val context = LocalContext.current
    var bedH by remember { mutableStateOf(23) }
    var bedM by remember { mutableStateOf(0) }
    var wakeH by remember { mutableStateOf(6) }
    var wakeM by remember { mutableStateOf(30) }
    var quality by remember { mutableStateOf(3) }

    val (bed, wake) = computeRange(bedH, bedM, wakeH, wakeM)
    val hours = (wake - bed) / 3_600_000f
    val valid = hours in 1f..16f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ghi nhận giấc ngủ") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { TimePickerDialog(context, { _, h, m -> bedH = h; bedM = m }, bedH, bedM, true).show() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Đi ngủ: %02d:%02d".format(bedH, bedM)) }
                OutlinedButton(
                    onClick = { TimePickerDialog(context, { _, h, m -> wakeH = h; wakeM = m }, wakeH, wakeM, true).show() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Thức dậy: %02d:%02d".format(wakeH, wakeM)) }

                Text("Chất lượng giấc ngủ (1 tệ → 5 tốt)", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { q ->
                        FilterChip(selected = quality == q, onClick = { quality = q }, label = { Text("$q") })
                    }
                }
                if (valid) Text("Tổng: ${"%.1f".format(hours)} giờ", fontWeight = FontWeight.SemiBold)
                else Text("Thời gian ngủ phải từ 1 đến 16 giờ", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(bed, wake, quality) }, enabled = valid) { Text("Lưu") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}