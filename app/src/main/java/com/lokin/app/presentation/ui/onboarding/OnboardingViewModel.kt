package com.lokin.app.presentation.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokin.app.data.auth.AuthStateProvider
import com.lokin.app.data.local.entity.UserEntity
import com.lokin.app.data.local.entity.WeightLogEntity
import com.lokin.app.domain.repository.UserRepository
import com.lokin.app.domain.repository.WeightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val weightRepository: WeightRepository,
    private val authState: AuthStateProvider
) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    fun saveProfile(
        name: String, age: Int, gender: String,
        heightCm: Float, weightKg: Float, goalWeightKg: Float
    ) {
        val uid = authState.currentUid ?: return
        viewModelScope.launch {
            userRepository.saveUser(
                UserEntity(
                    uid = uid, name = name, age = age, gender = gender,
                    heightCm = heightCm, weightKg = weightKg, goalWeightKg = goalWeightKg
                )
            )
            // Log cân nặng đầu tiên để tab Cân nặng không bị trống
            weightRepository.insert(
                WeightLogEntity(uid = uid, date = System.currentTimeMillis(), weightKg = weightKg)
            )
            _saved.value = true
        }
    }
}