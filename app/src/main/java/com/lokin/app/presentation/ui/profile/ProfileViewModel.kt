package com.lokin.app.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.UserEntity
import com.lokin.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(val user: UserEntity? = null, val loaded: Boolean = false)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid -> userRepository.observeUser(uid) }
        .map { ProfileUiState(user = it, loaded = true) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun save(
        name: String, age: Int, gender: String, heightCm: Float,
        weightKg: Float, goalWeightKg: Float, waterGoalMl: Int, burnGoalKcal: Int
    ) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            val base = uiState.value.user ?: UserEntity(uid = uid)
            userRepository.saveUser(
                base.copy(
                    name = name.trim(), age = age, gender = gender,
                    heightCm = heightCm, weightKg = weightKg,
                    goalWeightKg = goalWeightKg, dailyWaterGoalMl = waterGoalMl,
                    dailyCalorieBurnGoal = burnGoalKcal
                )
            )
        }
    }
}