package com.lokin.app.presentation.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.domain.repository.ExerciseRepository
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WaterRepository
import com.lokin.app.domain.repository.WeightRepository
import com.lokin.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

private const val DAY_MS = 24 * 60 * 60 * 1000L

data class HistoryUiState(
    val days: Int = 7,
    val from: Long = 0L,
    val to: Long = 0L,
    val weightPoints: List<Pair<Long, Float>> = emptyList(),
    val waterDaily: List<Float> = emptyList(),
    val burnDaily: List<Float> = emptyList(),
    val waterGoal: Int = 2000,
    val burnGoal: Int = 500
)

private fun dailySums(items: List<Pair<Long, Float>>, start: Long, days: Int): List<Float> {
    val sums = FloatArray(days)
    items.forEach { (t, v) ->
        val idx = ((t - start) / DAY_MS).toInt()
        if (t >= start && idx in 0 until days) sums[idx] += v
    }
    return sums.toList()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val weightRepository: WeightRepository,
    private val waterRepository: WaterRepository,
    private val exerciseRepository: ExerciseRepository,
    private val userRepository: UserRepository,
    authState: AuthStateProvider
) : ViewModel() {

    private val period = MutableStateFlow(7)

    fun setDays(days: Int) { period.value = days }

    val uiState: StateFlow<HistoryUiState> =
        combine(authState.uidFlow.filterNotNull(), period) { uid, days -> uid to days }
            .flatMapLatest { (uid, days) ->
                DateUtil.todayStartFlow().flatMapLatest { today ->
                    val from = today - (days - 1) * DAY_MS
                    combine(
                        weightRepository.observeSince(uid, from),
                        waterRepository.observeSince(uid, from),
                        exerciseRepository.observeAll(uid),
                        userRepository.observeUser(uid)
                    ) { weights, waters, exercises, user ->
                        HistoryUiState(
                            days = days,
                            from = from,
                            to = today + DAY_MS,
                            weightPoints = weights.map { it.date to it.weightKg },
                            waterDaily = dailySums(waters.map { it.date to it.amountMl.toFloat() }, from, days),
                            burnDaily = dailySums(exercises.map { it.date to it.caloriesBurned }, from, days),
                            waterGoal = user?.dailyWaterGoalMl ?: 2000,
                            burnGoal = user?.dailyCalorieBurnGoal ?: 500
                        )
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())
}
