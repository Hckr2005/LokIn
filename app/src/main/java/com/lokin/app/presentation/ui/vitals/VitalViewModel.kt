package com.lokin.app.presentation.ui.vitals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.VitalLogEntity
import com.lokin.app.domain.repository.VitalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VitalUiState(
    val logs: List<VitalLogEntity> = emptyList(),
    val latestHr: VitalLogEntity? = null,
    val latestBp: VitalLogEntity? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VitalViewModel @Inject constructor(
    private val repository: VitalRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<VitalUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid -> repository.observeRecent(uid) }
        .map { logs ->
            VitalUiState(
                logs = logs,
                latestHr = logs.firstOrNull { it.heartRate != null },
                latestBp = logs.firstOrNull { it.systolic != null && it.diastolic != null }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), VitalUiState())

    fun addLog(heartRate: Int?, systolic: Int?, diastolic: Int?) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            repository.insert(
                VitalLogEntity(
                    uid = uid, date = System.currentTimeMillis(),
                    heartRate = heartRate, systolic = systolic, diastolic = diastolic
                )
            )
        }
    }

    fun deleteLog(log: VitalLogEntity) {
        viewModelScope.launch { repository.delete(log) }
    }
}