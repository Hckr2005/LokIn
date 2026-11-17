package com.lokin.app.presentation.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.ExerciseLogEntity
import com.lokin.app.domain.repository.ExerciseRepository
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WeightRepository
import com.lokin.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DAY_MS = 24 * 60 * 60 * 1000L

data class ExercisePreset(val name: String, val met: Float)

// MET xấp xỉ theo Compendium of Physical Activities (cường độ trung bình)
val exercisePresets = listOf(
    ExercisePreset("Đi bộ", 3.5f),
    ExercisePreset("Chạy bộ", 9.8f),
    ExercisePreset("Đạp xe", 7.5f),
    ExercisePreset("Bơi lội", 6.0f),
    ExercisePreset("Yoga", 2.5f),
    ExercisePreset("Tập tạ", 5.0f),
    ExercisePreset("Nhảy dây", 11.0f),
    ExercisePreset("Cầu lông", 5.5f),
    ExercisePreset("Bóng đá", 7.0f),
)

data class ExerciseUiState(
    val logs: List<ExerciseLogEntity> = emptyList(),
    val todayCalories: Float = 0f,
    val goalCalories: Int = 500,
    val progress: Float = 0f,
    val weightKg: Float? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: ExerciseRepository,
    private val userRepository: UserRepository,
    private val weightRepository: WeightRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<ExerciseUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid ->
            DateUtil.todayStartFlow().flatMapLatest { start ->
                combine(
                    repository.observeAll(uid),
                    repository.observeDayCalories(uid, start, start + DAY_MS),
                    userRepository.observeUser(uid),
                    weightRepository.observeSince(uid, DateUtil.weeksAgo(12))
                ) { logs, today, user, weights ->
                    val weight = weights.lastOrNull()?.weightKg
                        ?: user?.weightKg?.takeIf { it > 0f }
                    val goal = (user?.dailyCalorieBurnGoal ?: 500).coerceAtLeast(1)
                    ExerciseUiState(
                        logs = logs,
                        todayCalories = today,
                        goalCalories = goal,
                        progress = (today / goal).coerceIn(0f, 1f),
                        weightKg = weight
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExerciseUiState())

    fun addLog(preset: ExercisePreset, durationMin: Int) {
        val uid = authState.currentUid ?: return
        val weight = uiState.value.weightKg ?: return
        viewModelScope.launch {
            repository.insert(
                ExerciseLogEntity(
                    uid = uid,
                    date = System.currentTimeMillis(),
                    exerciseType = preset.name,
                    durationMin = durationMin,
                    met = preset.met,
                    caloriesBurned = preset.met * weight * durationMin / 60f
                )
            )
        }
    }

    fun deleteLog(log: ExerciseLogEntity) {
        viewModelScope.launch { repository.delete(log) }
    }
}