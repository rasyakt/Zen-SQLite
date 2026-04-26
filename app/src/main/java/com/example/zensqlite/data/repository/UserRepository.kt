package com.example.zensqlite.data.repository

import com.example.zensqlite.data.dao.UserDao
import com.example.zensqlite.data.entity.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(fullName: String, email: String, password: String): Result<Long> {
        return try {
            val exists = userDao.isEmailExists(email)
            if (exists) {
                Result.failure(Exception("Email sudah terdaftar"))
            } else {
                val user = UserEntity(
                    fullName = fullName,
                    email = email,
                    password = password
                )
                val id = userDao.insertUser(user)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Email atau password salah"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return userDao.getUserById(id)
    }
}
