package com.lokin.app.data.repository

import com.lokin.app.data.local.dao.UserDao
import com.lokin.app.data.local.entity.UserEntity
import com.lokin.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {
    override fun observeUser(uid: String): Flow<UserEntity?> = userDao.observe(uid)
    override suspend fun saveUser(user: UserEntity) = userDao.upsert(user)
}