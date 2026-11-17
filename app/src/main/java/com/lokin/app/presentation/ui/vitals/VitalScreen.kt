package com.lokin.app.presentation.ui.vitals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lokin.app.data.local.entity.VitalLogEntity
import com.lokin.app.util.VitalLevel
import com.lokin.app.util.VitalStatus
import com.lokin.app.util.VitalUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun levelColor(level: VitalLevel): Color = when (level) {
    VitalLevel.NORMAL -> Color(0xFF0E9F6E)
    VitalLevel.WARNING -> Color(0xFFD97706)
    VitalLevel.DANGER -> MaterialTheme.colorScheme.error
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VitalScreen(onBack: () -> Unit, viewModel: VitalViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val hr = state.latestHr?.heartRate?.let { it to VitalUtil.heartRate(it) }
    val bp = state.latestBp?.let { (it.systolic!!) to (it.diastolic!!) }
        ?.let { (s, d) -> Triple(s, d, VitalUtil.bloodPressure(s, d)) }
    val anyDanger = hr?.second?.level == VitalLevel.DANGER || bp?.third?.level == VitalLevel.DANGER

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nhịp tim & huyết áp") },
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
            Row(
                Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VitalCard(
                    modifier = Modifier.weight(1f),
                    title = "Nhịp tim",
                    value = hr?.first?.let { "$it" } ?: "--",
                    unit = "lần/phút",
                    status = hr?.second
                )
                VitalCard(
                    modifier = Modifier.weight(1f),
                    title = "Huyết áp",
                    value = bp?.let { "${it.first}/${it.second}" } ?: "--",
                    unit = "mmHg",
                    status = bp?.third
                )
            }

            if (anyDanger) {
                Text(
                    "Chỉ số này ở mức cần chú ý. Hãy nghỉ ngơi vài phút rồi đo lại. Nếu vẫn cao hoặc có đau ngực, khó thở, chóng mặt, hãy liên hệ bác sĩ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text(
                "Mức tham khảo cho người trưởng thành lúc nghỉ, không thay thế tư vấn y tế.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("Lịch sử", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))

            if (state.logs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có dữ liệu. Nhấn + để thêm.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.logs, key = { it.id }) { log -> VitalLogItem(log) { viewModel.deleteLog(log) } }
                }
            }
        }
    }

    if (showDialog) {
        AddVitalDialog(
            onDismiss = { showDialog = false },
            onConfirm = { h, s, d ->
                viewModel.addLog(h, s, d)
                showDialog = false
            }
        )
    }
}

@Composable
private fun VitalCard(modifier: Modifier, title: String, value: String, unit: String, status: VitalStatus?) {
    Card(modifier) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            if (status != null) {
                Text(status.label, color = levelColor(status.level), fontWeight = FontWeight.SemiBold)
            } else {
                Text("Chưa có dữ liệu", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun VitalLogItem(log: VitalLogEntity, onDelete: () -> Unit) {
    val format = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                log.heartRate?.let {
                    val st = VitalUtil.heartRate(it)
                    Row {
                        Text("Nhịp tim: $it lần/phút · ", style = MaterialTheme.typography.bodyMedium)
                        Text(st.label, color = levelColor(st.level), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (log.systolic != null && log.diastolic != null) {
                    val st = VitalUtil.bloodPressure(log.systolic, log.diastolic)
                    Row {
                        Text("Huyết áp: ${log.systolic}/${log.diastolic} · ", style = MaterialTheme.typography.bodyMedium)
                        Text(st.label, color = levelColor(st.level), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Text(
                    format.format(Date(log.date)),
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

@Composable
private fun AddVitalDialog(onDismiss: () -> Unit, onConfirm: (Int?, Int?, Int?) -> Unit) {
    var hr by remember { mutableStateOf("") }
    var sys by remember { mutableStateOf("") }
    var dia by remember { mutableStateOf("") }

    val hrV = hr.toIntOrNull()?.takeIf { it in 30..220 }
    val sysV = sys.toIntOrNull()?.takeIf { it in 70..250 }
    val diaV = dia.toIntOrNull()?.takeIf { it in 40..150 }

    val hrOk = hr.isBlank() || hrV != null
    val bpBlank = sys.isBlank() && dia.isBlank()
    val bpOk = bpBlank || (sysV != null && diaV != null && sysV > diaV)
    val hasAny = hrV != null || (sysV != null && diaV != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ghi nhận chỉ số") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = hr, onValueChange = { hr = it.filter(Char::isDigit) },
                    label = { Text("Nhịp tim (lần/phút)") },
                    isError = !hrOk,
                    supportingText = { if (!hrOk) Text("Nhập từ 30 đến 220") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sys, onValueChange = { sys = it.filter(Char::isDigit) },
                    label = { Text("Huyết áp tâm thu (số trên)") },
                    isError = !bpOk && sys.isNotBlank() && sysV == null,
                    supportingText = { if (!bpOk && sys.isNotBlank() && sysV == null) Text("Nhập từ 70 đến 250") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dia, onValueChange = { dia = it.filter(Char::isDigit) },
                    label = { Text("Huyết áp tâm trương (số dưới)") },
                    isError = !bpOk && dia.isNotBlank() && diaV == null,
                    supportingText = { if (!bpOk && dia.isNotBlank() && diaV == null) Text("Nhập từ 40 đến 150") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                if (!bpOk && sysV != null && diaV != null) {
                    Text("Số trên phải lớn hơn số dưới", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "Có thể chỉ nhập nhịp tim, hoặc chỉ nhập huyết áp (cần đủ cả hai số).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(hrV, if (bpBlank) null else sysV, if (bpBlank) null else diaV)
                },
                enabled = hasAny && hrOk && bpOk
            ) { Text("Lưu") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Huỷ") } }
    )
}