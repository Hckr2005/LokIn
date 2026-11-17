package com.lokin.app.presentation.ui.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.WeightLogEntity
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WeightRepository
import com.lokin.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeightUiState(
    val logs: List<WeightLogEntity> = emptyList(),
    val latestWeight: Float? = null,
    val bmi: Float? = null,
    val bmiCategory: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class WeightViewModel @Inject constructor(
    private val repository: WeightRepository,
    private val userRepository: UserRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<WeightUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid ->
            combine(
                repository.observeSince(uid, DateUtil.weeksAgo(12)), // ASC: phần tử cuối là mới nhất
                userRepository.observeUser(uid)
            ) { logs, user ->
                val latest = logs.lastOrNull()?.weightKg
                val w = latest ?: user?.weightKg?.takeIf { it > 0f }
                val h = user?.heightCm ?: 0f
                val bmi = if (w != null && h > 0f) DateUtil.calcBmi(w, h) else null
                WeightUiState(
                    logs = logs,
                    latestWeight = latest,
                    bmi = bmi,
                    bmiCategory = bmi?.let { DateUtil.bmiCategory(it) } ?: ""
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeightUiState())

    fun addLog(weightKg: Float) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            repository.insert(
                WeightLogEntity(uid = uid, date = System.currentTimeMillis(), weightKg = weightKg)
            )
        }
    }

    fun deleteLog(log: WeightLogEntity) {
        viewModelScope.launch { repository.delete(log) }
    }
}