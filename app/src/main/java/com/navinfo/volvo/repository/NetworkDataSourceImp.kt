package com.navinfo.volvo.repository

import com.navinfo.volvo.database.AppDatabase
import com.navinfo.volvo.database.dao.GreetingMessageDao
import com.navinfo.volvo.di.scope.IoDispatcher
import com.navinfo.volvo.database.entity.GreetingMessage
import com.navinfo.volvo.http.DefaultResponse
import com.navinfo.volvo.model.messagelist.NetworkMessageListPost
import com.navinfo.volvo.model.messagelist.NetworkMessageListResponse
import com.navinfo.volvo.repository.service.NetworkService
import com.navinfo.volvo.tools.GsonUtil
import com.navinfo.volvo.util.NetResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject


class NetworkDataSourceImp @Inject constructor(
    private val netWorkService: NetworkService,
    private val messageDao: GreetingMessageDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NetworkDataSource {

    override suspend fun getCardList(message: NetworkMessageListPost): NetResult<DefaultResponse<NetworkMessageListResponse>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val stringBody = GsonUtil.getInstance().toJson(message)
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
                val result = netWorkService.queryCardListByApp(stringBody)
                if (result.isSuccessful) {
                    val body = result.body()
                    val list: MutableList<GreetingMessage> =
                        listOf(body!!.data!!.rows) as MutableList<GreetingMessage>
                    messageDao.insert(*list.map { it }.toTypedArray())
                    NetResult.Success(body)
                } else {
                    NetResult.Success(null)
                }
            } catch (e: Exception) {
                NetResult.Error(e)
            }
        }


}