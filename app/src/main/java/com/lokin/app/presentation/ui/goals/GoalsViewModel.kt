package com.lokin.app.presentation.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.UserEntity
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WeightRepository
import com.lokin.app.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalsUiState(
    val user: UserEntity? = null,
    val currentWeight: Float? = null
) {
    /** 0..1: đã đi được bao nhiêu từ cân nặng ban đầu tới mục tiêu. */
    val weightProgress: Float?
        get() {
            val u = user ?: return null
            val cur = currentWeight ?: return null
            if (u.weightKg <= 0f || u.goalWeightKg <= 0f) return null
            val total = u.weightKg - u.goalWeightKg
            if (kotlin.math.abs(total) < 0.1f) return 1f
            return ((u.weightKg - cur) / total).coerceIn(0f, 1f)
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GoalsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weightRepository: WeightRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    val uiState: StateFlow<GoalsUiState> = authState.uidFlow
        .filterNotNull()
        .flatMapLatest { uid ->
            combine(
                userRepository.observeUser(uid),
                weightRepository.observeSince(uid, DateUtil.weeksAgo(12))
            ) { user, logs -> GoalsUiState(user, logs.lastOrNull()?.weightKg) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalsUiState())

    fun save(goalWeightKg: Float, waterMl: Int, calorieIn: Int, steps: Int, calorieBurn: Int) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            val base = uiState.value.user ?: UserEntity(uid = uid)
            userRepository.saveUser(
                base.copy(
                    goalWeightKg = goalWeightKg,
                    dailyWaterGoalMl = waterMl,
                    dailyCalorieGoal = calorieIn,
                    dailyStepGoal = steps,
                    dailyCalorieBurnGoal = calorieBurn
                )
            )
        }
    }
}
