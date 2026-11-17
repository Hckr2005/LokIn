package com.lokin.app.presentation.ui.profile

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.lokin.app.presentation.ui.auth.AuthViewModel

private val genders = listOf("male" to "Nam", "female" to "Nữ", "other" to "Khác")
private fun String.toNum() = replace(',', '.').toFloatOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user = viewModel.uiState.collectAsState().value.user

    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var age by remember(user) { mutableStateOf(user?.age?.takeIf { it > 0 }?.toString() ?: "") }
    var gender by remember(user) { mutableStateOf(user?.gender?.ifBlank { "male" } ?: "male") }
    var height by remember(user) { mutableStateOf(user?.heightCm?.takeIf { it > 0 }?.let { "%.0f".format(it) } ?: "") }
    var weight by remember(user) { mutableStateOf(user?.weightKg?.takeIf { it > 0 }?.let { "%.1f".format(it) } ?: "") }
    var goalW by remember(user) { mutableStateOf(user?.goalWeightKg?.takeIf { it > 0 }?.let { "%.1f".format(it) } ?: "") }
    var water by remember(user) { mutableStateOf((user?.dailyWaterGoalMl ?: 2000).toString()) }
    var burn by remember(user) { mutableStateOf((user?.dailyCalorieBurnGoal ?: 500).toString()) }

    val ageV = age.toIntOrNull()?.takeIf { it in 10..100 }
    val heightV = height.toNum()?.takeIf { it in 100f..250f }
    val weightV = weight.toNum()?.takeIf { it in 20f..300f }
    val goalV = goalW.toNum()?.takeIf { it in 20f..300f }
    val waterV = water.toIntOrNull()?.takeIf { it in 500..6000 }
    val burnV = burn.toIntOrNull()?.takeIf { it in 50..3000 }
    val canSave = name.isNotBlank() && ageV != null && heightV != null &&
            weightV != null && goalV != null && waterV != null && burnV != null

    Scaffold(topBar = { TopAppBar(title = { Text("Hồ sơ cá nhân") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Field("Họ và tên", name, { name = it }, false, "", KeyboardType.Text, filter = false)
            Field("Tuổi", age, { age = it }, age.isNotBlank() && ageV == null, "10 – 100", KeyboardType.Number)

            Text("Giới tính", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                genders.forEach { (key, label) ->
                    FilterChip(selected = gender == key, onClick = { gender = key }, label = { Text(label) })
                }
            }

            Field("Chiều cao (cm)", height, { height = it }, height.isNotBlank() && heightV == null, "100 – 250 cm")
            Field("Cân nặng ban đầu (kg)", weight, { weight = it }, weight.isNotBlank() && weightV == null, "20 – 300 kg")
            Field("Cân nặng mục tiêu (kg)", goalW, { goalW = it }, goalW.isNotBlank() && goalV == null, "20 – 300 kg")
            Field(
                "Mục tiêu nước mỗi ngày (ml)", water, { water = it },
                water.isNotBlank() && waterV == null, "500 – 6000 ml", KeyboardType.Number
            )
            if (weightV != null) {
                TextButton(onClick = { water = ((weightV * 35 / 50).toInt() * 50).toString() }) {
                    Text("Gợi ý theo cân nặng (35 ml/kg)")
                }
            }
            Field(
                "Mục tiêu calo tiêu hao mỗi ngày (kcal)", burn, { burn = it },
                burn.isNotBlank() && burnV == null, "50 – 3000 kcal", KeyboardType.Number
            )

            Button(
                onClick = {
                    viewModel.save(name, ageV!!, gender, heightV!!, weightV!!, goalV!!, waterV!!, burnV!!)
                    Toast.makeText(context, "Đã lưu hồ sơ", Toast.LENGTH_SHORT).show()
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Lưu hồ sơ") }

            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    val activity = context as Activity
                    activity.finish()
                    activity.startActivity(activity.intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Đăng xuất") }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Field(
    label: String, value: String, onChange: (String) -> Unit,
    isError: Boolean, hint: String,
    keyboard: KeyboardType = KeyboardType.Decimal,
    filter: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            onChange(if (filter) it.filter { c -> c.isDigit() || c == '.' || c == ',' } else it)
        },
        label = { Text(label) },
        isError = isError,
        supportingText = { if (isError) Text("Nhập trong khoảng $hint") },
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}