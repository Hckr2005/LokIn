package com.lokin.app.presentation.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.domain.repository.ExerciseRepository
import com.lokin.app.domain.repository.SleepRepository
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WaterRepository
import com.lokin.app.domain.repository.WeightRepository
import com.lokin.app.util.DateUtil
import com.lokin.app.util.SleepUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val DAY_MS = 24 * 60 * 60 * 1000L

data class DashboardUiState(
    val loaded: Boolean = false,
    val name: String = "",
    val profileIncomplete: Boolean = false,
    val waterMl: Int = 0,
    val waterGoalMl: Int = 2000,
    val waterProgress: Float = 0f,
    val weightKg: Float? = null,
    val bmi: Float? = null,
    val bmiCategory: String = "",
    val burnKcal: Float = 0f,
    val burnGoal: Int = 500,
    val burnProgress: Float = 0f,
    val sleepHours: Float? = null,
    val sleepQuality: Int = 0,
    val sleepMin: Int = 7,
    val sleepMax: Int = 9
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weightRepository: WeightRepository,
    private val waterRepository: WaterRepository,
    private val exerciseRepository: ExerciseRepository,
    private val sleepRepository: SleepRepository,
    authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid ->
            DateUtil.todayStartFlow().flatMapLatest { start ->
                combine(
                    userRepository.observeUser(uid),
                    weightRepository.observeSince(uid, DateUtil.weeksAgo(12)),
                    waterRepository.observeDayTotal(uid, start, start + DAY_MS),
                    exerciseRepository.observeDayCalories(uid, start, start + DAY_MS),
                    sleepRepository.observeRecent(uid)
                ) { user, logs, water, burn, sleeps ->
                    val weight = logs.lastOrNull()?.weightKg ?: user?.weightKg?.takeIf { it > 0f }
                    val h = user?.heightCm ?: 0f
                    val bmi = if (weight != null && h > 0f) DateUtil.calcBmi(weight, h) else null
                    val waterGoal = (user?.dailyWaterGoalMl ?: 2000).coerceAtLeast(1)
                    val burnGoal = (user?.dailyCalorieBurnGoal ?: 500).coerceAtLeast(1)
                    val (sMin, sMax) = SleepUtil.recommended(user?.age ?: 0)
                    val lastSleep = sleeps.firstOrNull()
                    DashboardUiState(
                        loaded = true,
                        name = user?.name ?: "",
                        profileIncomplete = user == null || h <= 0f || user.weightKg <= 0f,
                        waterMl = water,
                        waterGoalMl = waterGoal,
                        waterProgress = (water.toFloat() / waterGoal).coerceIn(0f, 1f),
                        weightKg = weight,
                        bmi = bmi,
                        bmiCategory = bmi?.let { DateUtil.bmiCategory(it) } ?: "",
                        burnKcal = burn,
                        burnGoal = burnGoal,
                        burnProgress = (burn / burnGoal).coerceIn(0f, 1f),
                        sleepHours = lastSleep?.let { SleepUtil.hours(it) },
                        sleepQuality = lastSleep?.quality ?: 0,
                        sleepMin = sMin,
                        sleepMax = sMax
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}