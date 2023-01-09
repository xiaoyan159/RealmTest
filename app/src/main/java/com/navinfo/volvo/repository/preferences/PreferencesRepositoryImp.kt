package com.navinfo.volvo.repository.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.navinfo.volvo.model.proto.LoginUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


const val DataStore_NAME = "ShardPreferences"
val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = DataStore_NAME)

class PreferencesRepositoryImp @Inject constructor(
    private val context: Context,
    private val loginUser: DataStore<LoginUser>
) : PreferencesRepository {

    companion object {
        val NAME = stringPreferencesKey("NAME")
        val PHONE_NUMBER = stringPreferencesKey("PHONE")
        val address = stringPreferencesKey("ADDRESS")
    }

    override suspend fun saveLoginUser(id: String, name: String, password: String) {
        loginUser.updateData { preference ->
            preference.toBuilder().setUsername(name).setPassword(password).build()
        }
    }

    override suspend fun saveString(key: String, content: String) {
        context.datastore.edit {
            it[stringPreferencesKey(key)] = content
        }
    }

    override suspend fun getString(key: String): Flow<String?> = context.datastore.data.map {
        it[stringPreferencesKey(key)]
    }

    override suspend fun saveInt(key: String, content: Int) {
        context.datastore.edit {
            it[intPreferencesKey(key)] = content
        }
    }

    override suspend fun getInt(key: String): Flow<Int?> = context.datastore.data.map {
        it[intPreferencesKey(key)]
    }

    override fun loginUser(): Flow<LoginUser?> = loginUser.data

}