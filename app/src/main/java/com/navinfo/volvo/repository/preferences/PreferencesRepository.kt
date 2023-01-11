package com.navinfo.volvo.repository.preferences

import com.navinfo.volvo.model.proto.LoginUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * 数据库操作接口
 */
interface PreferencesRepository {

    suspend fun saveLoginUser(id: String, name: String, password: String)
    fun loginUser(): Flow<LoginUser?>
    suspend fun saveString(key: String, content: String)
    suspend fun getString(key: String): Flow<String?>
    suspend fun saveInt(key: String, content: Int)
    suspend fun getInt(key: String): Flow<Int?>
}