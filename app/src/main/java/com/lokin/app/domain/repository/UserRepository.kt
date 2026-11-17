package com.lokin.app.domain.repository

import com.lokin.app.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUser(uid: String): Flow<UserEntity?>
    suspend fun saveUser(user: UserEntity)
}