package com.lokin.app.presentation.ui.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lokin.app.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    fun go(route: String) = navController.navigate(route) { launchSingleTop = true }

    Scaffold(topBar = {
        TopAppBar(title = { Text(if (state.name.isBlank()) "Trang chủ" else "Xin chào, ${state.name}") })
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            if (state.loaded && state.profileIncomplete) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().clickable { go(Screen.Profile.route) }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Hoàn thiện hồ sơ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                        Text(
                            "Nhập chiều cao và cân nặng để tính BMI chính xác.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Nước uống
            Card(
                modifier = Modifier.fillMaxWidth().clickable { go(Screen.Water.route) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Nước uống hôm nay", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(
                        "${state.waterMl} / ${state.waterGoalMl} ml",
                        style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { state.waterProgress }, modifier = Modifier.fillMaxWidth().height(8.dp))
                }
            }

            // Vận động
            Card(
                modifier = Modifier.fillMaxWidth().clickable { go(Screen.Exercise.route) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Vận động hôm nay", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onTertiaryContainer)
                    Text(
                        "${state.burnKcal.toInt()} / ${state.burnGoal} kcal",
                        style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(progress = { state.burnProgress }, modifier = Modifier.fillMaxWidth().height(8.dp))
                }
            }

            // Cân nặng
            Card(
                modifier = Modifier.fillMaxWidth().clickable { go(Screen.Weight.route) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Cân nặng", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(
                        state.weightKg?.let { "${"%.1f".format(it)} kg" } ?: "Chưa có dữ liệu",
                        style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    state.bmi?.let {
                        Text(
                            "BMI ${"%.1f".format(it)} · ${state.bmiCategory}",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Giấc ngủ
            Card(
                modifier = Modifier.fillMaxWidth().clickable { go(Screen.Sleep.route) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Giấc ngủ đêm qua", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        state.sleepHours?.let { "${"%.1f".format(it)} giờ · Chất lượng ${state.sleepQuality}/5" } ?: "Chưa có dữ liệu",
                        style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Khuyến nghị ${state.sleepMin}–${state.sleepMax} giờ",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Mục tiêu
            OutlinedButton(onClick = { go(Screen.History.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Lịch sử & xu hướng")
            }
            OutlinedButton(onClick = { go(Screen.Vitals.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Nhịp tim & huyết áp")
            }
            OutlinedButton(onClick = { go(Screen.Goals.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Đặt & theo dõi mục tiêu")
            }


            Spacer(Modifier.height(16.dp))
        }
    }
}