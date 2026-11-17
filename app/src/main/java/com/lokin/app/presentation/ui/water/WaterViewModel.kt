package com.lokin.app.presentation.ui.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.WaterLogEntity
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WaterRepository
import com.lokin.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DAY_MS = 24 * 60 * 60 * 1000L

data class WaterUiState(
    val todayMl: Int = 0,
    val goalMl: Int = 2000,
    val progress: Float = 0f,
    val percent: Int = 0,
    val todayLogs: List<WaterLogEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WaterViewModel @Inject constructor(
    private val repository: WaterRepository,
    private val userRepository: UserRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<WaterUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid ->
            DateUtil.todayStartFlow().flatMapLatest { start ->
                combine(
                    repository.observeSince(uid, start),
                    userRepository.observeUser(uid)
                ) { logs, user ->
                    val today = logs.filter { it.date < start + DAY_MS } // DESC: mới nhất trước
                    val total = today.sumOf { it.amountMl }
                    val goal = (user?.dailyWaterGoalMl ?: 2000).coerceAtLeast(1)
                    WaterUiState(
                        todayMl = total,
                        goalMl = goal,
                        progress = (total.toFloat() / goal).coerceIn(0f, 1f),
                        percent = (total * 100f / goal).toInt(),
                        todayLogs = today
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WaterUiState())

    fun addWater(ml: Int) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            repository.insert(
                WaterLogEntity(uid = uid, date = System.currentTimeMillis(), amountMl = ml)
            )
        }
    }

    fun deleteLog(log: WaterLogEntity) {
        viewModelScope.launch { repository.delete(log) }
    }
}