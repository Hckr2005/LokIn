package com.lokin.app.presentation.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private fun String.toNum() = replace(',', '.').toFloatOrNull()

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val saved by viewModel.saved.collectAsState()
    LaunchedEffect(saved) { if (saved) onComplete() }

    var name        by remember { mutableStateOf("") }
    var age         by remember { mutableStateOf("") }
    var gender      by remember { mutableStateOf("male") }
    var heightCm    by remember { mutableStateOf("") }
    var weightKg    by remember { mutableStateOf("") }
    var goalWeight  by remember { mutableStateOf("") }

    val ageV    = age.toIntOrNull()?.takeIf { it in 10..100 }
    val heightV = heightCm.toNum()?.takeIf { it in 100f..250f }
    val weightV = weightKg.toNum()?.takeIf { it in 20f..300f }
    val goalV   = goalWeight.toNum()?.takeIf { it in 20f..300f }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hồ sơ cá nhân",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
            Text("Giúp chúng tôi cá nhân hóa trải nghiệm của bạn",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Họ và tên") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = age, onValueChange = { age = it },
                label = { Text("Tuổi") },
                isError = age.isNotBlank() && ageV == null,
                supportingText = { if (age.isNotBlank() && ageV == null) Text("Nhập từ 10 đến 100") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            Text("Giới tính", style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start))
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("male" to "Nam", "female" to "Nữ", "other" to "Khác").forEach { (value, label) ->
                    FilterChip(
                        selected = gender == value,
                        onClick = { gender = value },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = heightCm, onValueChange = { heightCm = it },
                label = { Text("Chiều cao (cm)") },
                isError = heightCm.isNotBlank() && heightV == null,
                supportingText = { if (heightCm.isNotBlank() && heightV == null) Text("Nhập từ 100 đến 250 cm") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = weightKg, onValueChange = { weightKg = it },
                label = { Text("Cân nặng hiện tại (kg)") },
                isError = weightKg.isNotBlank() && weightV == null,
                supportingText = { if (weightKg.isNotBlank() && weightV == null) Text("Nhập từ 20 đến 300 kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = goalWeight, onValueChange = { goalWeight = it },
                label = { Text("Cân nặng mục tiêu (kg, tuỳ chọn)") },
                isError = goalWeight.isNotBlank() && goalV == null,
                supportingText = { if (goalWeight.isNotBlank() && goalV == null) Text("Nhập từ 20 đến 300 kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.saveProfile(name.trim(), ageV!!, gender, heightV!!, weightV!!, goalV ?: weightV)
                },
                enabled = name.isNotBlank() && ageV != null && heightV != null && weightV != null &&
                        (goalWeight.isBlank() || goalV != null),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Bắt đầu →")
            }
        }
    }
}