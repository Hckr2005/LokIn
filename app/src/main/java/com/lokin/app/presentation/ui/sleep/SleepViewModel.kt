package com.lokin.app.presentation.ui.sleep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.SleepLogEntity
import com.lokin.app.domain.repository.SleepRepository
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.util.SleepUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SleepUiState(
    val logs: List<SleepLogEntity> = emptyList(),
    val avgHours: Float? = null,
    val recMin: Int = 7,
    val recMax: Int = 9
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SleepViewModel @Inject constructor(
    private val repository: SleepRepository,
    private val userRepository: UserRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<SleepUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid ->
            combine(repository.observeRecent(uid), userRepository.observeUser(uid)) { logs, user ->
                val (min, max) = SleepUtil.recommended(user?.age ?: 0)
                val last7 = logs.take(7)
                SleepUiState(
                    logs = logs,
                    avgHours = if (last7.isEmpty()) null else last7.map { SleepUtil.hours(it) }.average().toFloat(),
                    recMin = min,
                    recMax = max
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SleepUiState())

    fun addLog(bedtimeMs: Long, wakeMs: Long, quality: Int) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            // date = thời điểm thức dậy, để tính "đêm qua" theo ngày thức dậy
            repository.insert(
                SleepLogEntity(uid = uid, date = wakeMs, bedtimeMs = bedtimeMs, wakeTimeMs = wakeMs, quality = quality)
            )
        }
    }

    fun deleteLog(log: SleepLogEntity) {
        viewModelScope.launch { repository.delete(log) }
    }
}